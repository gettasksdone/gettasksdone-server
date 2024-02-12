package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.gettasksdone.repository.ContextoRepository;
import com.gettasksdone.repository.EtiquetaRepository;


@RestController
@RequestMapping("/task")
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
    
    @GetMapping("/getTasks")
	public ResponseEntity<List<Tarea>> allTasks(){
		return new ResponseEntity<>(tareaRepo.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(task.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody Tarea task, @RequestParam("ProjectID") long projectID){
        Optional<Proyecto> project = proyectoRepo.findById(projectID);
        Tarea tarea;
        List<Tarea> projectTasks;
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Usuario> user = usuarioRepo.findById(project.get().getUsuario().getId());
            Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
            if(context.isEmpty()){
                return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
            }else{
                task.setContexto(context.get());
                task.setUsuario(user.get());
                tarea = tareaRepo.save(task);
                projectTasks = project.get().getTareas();
                projectTasks.add(tarea);
                project.get().setTareas(projectTasks);
                proyectoRepo.save(project.get());
                return new ResponseEntity<>(tarea, HttpStatus.OK);
            }
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateTask(@RequestBody Tarea task, @PathVariable("id") Long id){
        Optional<Tarea> tarea = tareaRepo.findById(id);
        Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
        if(tarea.isEmpty()){
            return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
        }
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found", HttpStatus.BAD_REQUEST);
        }
        tarea.get().setDescripcion(task.getDescripcion());
        tarea.get().setVencimiento(task.getVencimiento());
        tarea.get().setEstado(task.getEstado());
        tarea.get().setPrioridad(task.getPrioridad());
        tarea.get().setContexto(context.get());
        return new ResponseEntity<>(tareaRepo.save(tarea.get()), HttpStatus.OK);
        
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long id){
        if(tareaRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }else{
            tareaRepo.deleteById(id);
            return new ResponseEntity<>("Task deleted", HttpStatus.OK);
        }
    }

    @PatchMapping("/addTag/{id}")
    public ResponseEntity<?> addTagToTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                List<Etiqueta> tagList = task.get().getEtiquetas();
                if(tagList.contains(tag.get())){
                    return new ResponseEntity<>("Tag already on this task.", HttpStatus.BAD_REQUEST);
                }
                tagList.add(tag.get());
                task.get().setEtiquetas(tagList);
                return new ResponseEntity<>(tareaRepo.save(task.get()), HttpStatus.OK);
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    public ResponseEntity<?> delTagFromTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                List<Etiqueta> tagList = task.get().getEtiquetas();
                if(!tagList.contains(tag.get())){
                    return new ResponseEntity<>("Tag not present on this task.", HttpStatus.BAD_REQUEST);
                }
                tagList.remove(tag.get());
                task.get().setEtiquetas(tagList);
                return new ResponseEntity<>(tareaRepo.save(task.get()), HttpStatus.OK);
            }
        }
    }
}
