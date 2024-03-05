package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.TareaService;
import com.gettasksdone.utils.MHelpers;
import jakarta.servlet.http.HttpServletRequest;
import com.gettasksdone.repository.ContextoRepository;
import com.gettasksdone.repository.EtiquetaRepository;


@RestController
@RequestMapping("/task")
@SuppressWarnings("null")
public class TasksController {
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private ContextoRepository contextoRepo;
    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private TareaService tareaService;

    @GetMapping("/")
    public ResponseEntity<?> tasksFromUser(HttpServletRequest request) {
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(tareaService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getTasks")
	public ResponseEntity<?> allTasks(){
		return new ResponseEntity<>(tareaService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = task.get().getUsuario().getId();
            Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
            if(MHelpers.checkAccess(ownerID, authedUser.get())){
                return new ResponseEntity<>(tareaService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Task not found.", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody Tarea task, @RequestParam("ProjectID") long projectID, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(projectID);
        Tarea tarea;
        List<Tarea> projectTasks;
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Long ownerID = project.get().getUsuario().getId();
            Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
            if(MHelpers.checkAccess(ownerID, authedUser.get())){
                Optional<Usuario> user = usuarioRepo.findById(ownerID);
                Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
                if(context.isEmpty()){
                    return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
                }else{
                    task.setContexto(context.get());
                    task.setUsuario(user.get());
                    task.setProyecto(project.get());
                    tarea = tareaRepo.save(task);
                    projectTasks = project.get().getTareas();
                    projectTasks.add(tarea);
                    project.get().setTareas(projectTasks);
                    proyectoRepo.save(project.get());
                    return new ResponseEntity<>("Task created.", HttpStatus.OK);
                }
            }else{
                return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateTask(@RequestBody Tarea task, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> tarea = tareaRepo.findById(id);
        Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
        if(tarea.isEmpty()){
            return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
        }
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found", HttpStatus.BAD_REQUEST);
        }
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        Long ownerID = tarea.get().getUsuario().getId();
        if(MHelpers.checkAccess(ownerID, authedUser.get())){
            tarea.get().setDescripcion(task.getDescripcion());
            tarea.get().setVencimiento(task.getVencimiento());
            tarea.get().setEstado(task.getEstado());
            tarea.get().setPrioridad(task.getPrioridad());
            tarea.get().setContexto(context.get());
            tareaRepo.save(tarea.get());
            return new ResponseEntity<>("Task updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = task.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                tareaRepo.deleteById(id);
                return new ResponseEntity<>("Task deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PatchMapping("/addTag/{id}")
    public ResponseEntity<?> addTagToTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                Long ownerID = task.get().getUsuario().getId();
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                if(MHelpers.checkAccess(ownerID, authedUser)){
                    List<Etiqueta> tagList = task.get().getEtiquetas();
                    if(tagList.contains(tag.get())){
                        return new ResponseEntity<>("Tag already on this task.", HttpStatus.BAD_REQUEST);
                    }
                    tagList.add(tag.get());
                    task.get().setEtiquetas(tagList);
                    tareaRepo.save(task.get());
                    return new ResponseEntity<>("Tag added to task.", HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    public ResponseEntity<?> delTagFromTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                Long ownerID = task.get().getUsuario().getId();
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                if(MHelpers.checkAccess(ownerID, authedUser)){
                    List<Etiqueta> tagList = task.get().getEtiquetas();
                    if(!tagList.contains(tag.get())){
                        return new ResponseEntity<>("Tag not present on this task.", HttpStatus.BAD_REQUEST);
                    }
                    tagList.remove(tag.get());
                    task.get().setEtiquetas(tagList);
                    tareaRepo.save(task.get());
                    return new ResponseEntity<>("Tag deleted from task.", HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }
}
