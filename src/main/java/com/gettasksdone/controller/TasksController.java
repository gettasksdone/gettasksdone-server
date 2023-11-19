package com.gettasksdone.controller;

import java.time.LocalDateTime;
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
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.ContextoRepository;


@RestController
@RequestMapping("/task")
public class TasksController {
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private ContextoRepository contextoRepo;
    
    @GetMapping("/getTasks")
	public List<Tarea> allTasks(){
		return tareaRepo.findAll();
	}

    @GetMapping("/{id}")
    public Optional<Tarea> findById(@PathVariable("id") Long id){
        return tareaRepo.findById(id);
    }

    @PostMapping("/create")
    public Tarea createTask(@RequestBody Tarea task){
        Optional<Proyecto> project = proyectoRepo.findById(task.getProyecto().getId());
        task.setProyecto(project.get());
        Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
        task.setContexto(context.get());
        return tareaRepo.save(task);
    }
}
