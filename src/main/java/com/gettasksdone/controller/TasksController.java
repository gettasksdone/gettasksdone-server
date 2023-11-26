package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.gettasksdone.repository.ContextoRepository;
import com.gettasksdone.repository.EtiquetaRepository;
import com.gettasksdone.repository.UsuarioRepository;


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
	public List<Tarea> allTasks(){
		return tareaRepo.findAll();
	}

    @GetMapping("/{id}")
    public Optional<Tarea> findById(@PathVariable("id") Long id){
        return tareaRepo.findById(id);
    }

    @PostMapping("/create")
    public Tarea createTask(@RequestBody Tarea task, @RequestParam("ProjectID") long projectID){
        Optional<Proyecto> project = proyectoRepo.findById(projectID);
        Tarea tarea;
        List<Tarea> projectTasks;
        if(project.isEmpty()){
            return null;
        }else{
            Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
            if(context.isEmpty()){
                return null;
            }else{
                task.setContexto(context.get());
                tarea = tareaRepo.save(task);
                projectTasks = project.get().getTareas();
                projectTasks.add(tarea);
                project.get().setTareas(projectTasks);
                proyectoRepo.save(project.get());
                return tarea;
            }
        }
    }

    @PatchMapping("/update/{id}")
    public Tarea updateTask(@RequestBody Tarea task, @PathVariable("id") Long id){
        Optional<Tarea> tarea = tareaRepo.findById(id);
        Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
        if(tarea.isEmpty() || context.isEmpty()){
            return null;
        }
        tarea.get().setDescripcion(task.getDescripcion());
        tarea.get().setVencimiento(task.getVencimiento());
        tarea.get().setEstado(task.getEstado());
        tarea.get().setPrioridad(task.getPrioridad());
        tarea.get().setContexto(context.get());
        return tareaRepo.save(tarea.get());
        
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id){
        if(tareaRepo.findById(id).isEmpty()){
            return "Task not found";
        }else{
            tareaRepo.deleteById(id);
            return "Task deleted";
        }
    }

    @PatchMapping("/addTag/{id}")
    public Tarea addTagToTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return null;
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return null;
            }else{
                List<Etiqueta> tagList = task.get().getEtiquetas();
                if(tagList.contains(tag.get())){
                    return null;
                }
                tagList.add(tag.get());
                task.get().setEtiquetas(tagList);
                return tareaRepo.save(task.get());
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    public Tarea delTagFromTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return null;
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return null;
            }else{
                List<Etiqueta> tagList = task.get().getEtiquetas();
                if(!tagList.contains(tag.get())){
                    return null;
                }
                tagList.remove(tag.get());
                task.get().setEtiquetas(tagList);
                return tareaRepo.save(task.get());
            }
        }
    }

    @PatchMapping("/addUser/{id}")
    public Tarea addUserToTask(@RequestParam("UserID") Long userId, @PathVariable("id") Long id){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return null;
        }
        Optional<Usuario> user = usuarioRepo.findById(userId);
        if(user.isEmpty()){
            return null;
        }
        List<Usuario> userList = task.get().getUsuarios();
        if(userList.contains(user.get())){
            return null;
        }
        userList.add(user.get());
        task.get().setUsuarios(userList);
        return tareaRepo.save(task.get());
    }

    @PatchMapping("/removeUser/{id}")
    public Tarea delUserFromTask(@RequestParam("UserID") Long userId, @PathVariable("id") Long id){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return null;
        }
        Optional<Usuario> user = usuarioRepo.findById(userId);
        if(user.isEmpty()){
            return null;
        }
        List<Usuario> userList = task.get().getUsuarios();
        if(!userList.contains(user.get())){
            return null;
        }
        userList.remove(user.get());
        task.get().setUsuarios(userList);
        return tareaRepo.save(task.get());
    }
}
