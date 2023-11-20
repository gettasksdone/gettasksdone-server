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
import com.gettasksdone.model.Nota;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.repository.NotaRepository;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.TareaRepository;

@RestController
@RequestMapping("/note")
public class NoteController {
    @Autowired
    private NotaRepository notaRepo;
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private TareaRepository tareaRepo;

    @GetMapping("/getNotes")
	public List<Nota> allNotes(){
		return notaRepo.findAll();
	}

    @GetMapping("/{id}")
    public Optional<Nota> findById(@PathVariable("id") Long id){
        return notaRepo.findById(id);
    }

    @PostMapping("/create")
    public Nota createNote(@RequestParam("Target") String target, @RequestParam("ID") Long id, @RequestBody Nota note){
        Nota nota;
        List<Nota> notas;
        nota = notaRepo.save(note);
        if(target.equals("Project")){
            Optional<Proyecto> project = proyectoRepo.findById(id);
            if(project.isEmpty()){
                return null;
            }else{
                notas = project.get().getNotas();
                notas.add(nota);
                project.get().setNotas(notas);
                proyectoRepo.save(project.get());
            }
        }else if(target.equals("Task")){
            Optional<Tarea> task = tareaRepo.findById(id);
            if(task.isEmpty()){
                return null;
            }else{
                notas = task.get().getNotas();
                notas.add(nota);
                task.get().setNotas(notas);
                tareaRepo.save(task.get());
            }
        }else{
            return null;
        }
        return nota;
    }

    @PatchMapping("/update/{id}")
    public Nota updateNote(@PathVariable("id") Long id, @RequestBody Nota note){
        return notaRepo.save(note);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteNote(@PathVariable("id") Long id){
        if(notaRepo.findById(id).isEmpty()){
            return "Note not found";
        }else{
            notaRepo.deleteById(id);
            return "Note deleted";
        }
    }
}