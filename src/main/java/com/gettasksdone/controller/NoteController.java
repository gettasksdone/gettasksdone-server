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
	public ResponseEntity<List<Nota>> allNotes(){
		return new ResponseEntity<>(notaRepo.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<Nota> note = notaRepo.findById(id);
        if(note.isEmpty()){
            return new ResponseEntity<>("Note not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(note.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNote(@RequestParam("Target") String target, @RequestParam("ID") Long id, @RequestBody Nota note){
        Nota nota;
        List<Nota> notas;
        if(target.equals("Project")){
            Optional<Proyecto> project = proyectoRepo.findById(id);
            if(project.isEmpty()){
                return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
            }else{
                nota = notaRepo.save(note);
                notas = project.get().getNotas();
                notas.add(nota);
                project.get().setNotas(notas);
                proyectoRepo.save(project.get());
            }
        }else if(target.equals("Task")){
            Optional<Tarea> task = tareaRepo.findById(id);
            if(task.isEmpty()){
                return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
            }else{
                nota = notaRepo.save(note);
                notas = task.get().getNotas();
                notas.add(nota);
                task.get().setNotas(notas);
                tareaRepo.save(task.get());
            }
        }else{
            return new ResponseEntity<>("Must assign the note to a task or a project.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(nota, HttpStatus.OK);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateNote(@PathVariable("id") Long id, @RequestBody Nota note){
        Optional<Nota> nota = notaRepo.findById(id);
        if(nota.isEmpty()){
            return new ResponseEntity<>("Note not found.", HttpStatus.BAD_REQUEST);
        }
        nota.get().setContenido(note.getContenido());
        return new ResponseEntity<>(notaRepo.save(nota.get()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable("id") Long id){
        if(notaRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Note not found", HttpStatus.NOT_FOUND);
        }else{
            notaRepo.deleteById(id);
            return new ResponseEntity<>("Note deleted", HttpStatus.OK);
        }
    }
}