package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.repository.ProyectoRepository;

@RestController
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ProyectoRepository proyectoRepo;

    @GetMapping("/getProjects")
	public List<Proyecto> allProjects(){
		return proyectoRepo.findAll();
	}

    @GetMapping("/{id}")
    public Optional<Proyecto> findById(@PathVariable("id") Long id){
        return proyectoRepo.findById(id);
    }

    @PostMapping("/create")
    public Proyecto createProject(@RequestBody Proyecto project){
        return proyectoRepo.save(project);
    }
    
    @PatchMapping("/update/{id}")
    public Proyecto updateProject(@PathVariable("id") Long id, @RequestBody Proyecto project){
        return proyectoRepo.save(project);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id){
        if(proyectoRepo.findById(id).isEmpty()){
            return "Project not found";
        }else{
            proyectoRepo.deleteById(id);
            return "Project deleted";
        }
    }
}
