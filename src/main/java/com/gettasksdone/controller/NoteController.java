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
import com.gettasksdone.model.Nota;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.NotaRepository;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.NotaService;
import com.gettasksdone.utils.MHelpers;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/note")
public class NoteController {
    @Autowired
    private NotaRepository notaRepo;
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private NotaService notaService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getNotes")
	public ResponseEntity<?> allNotes(){
		return new ResponseEntity<>(notaService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Nota> note = notaRepo.findById(id);
        if(note.isEmpty()){
            return new ResponseEntity<>("Note not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = note.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(notaService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Note not found.", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNote(@RequestParam("Target") String target, @RequestParam("ID") Long id, @RequestBody Nota note, HttpServletRequest request){
        Nota nota;
        List<Nota> notas;
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        Long ownerID;
        if(target.equals("Project")){
            Optional<Proyecto> project = proyectoRepo.findById(id);
            if(project.isEmpty()){
                return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
            }else{
                ownerID = project.get().getUsuario().getId();
                if(!MHelpers.checkAccess(ownerID, authedUser)){
                    return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
                }
                note.setUsuario(authedUser);
                note.setProyecto(project.get());
                note.setTarea(null);
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
                ownerID = task.get().getUsuario().getId();
                if(!MHelpers.checkAccess(ownerID, authedUser)){
                    return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
                }
                note.setUsuario(authedUser);
                note.setProyecto(null);
                note.setTarea(task.get());
                nota = notaRepo.save(note);
                notas = task.get().getNotas();
                notas.add(nota);
                task.get().setNotas(notas);
                tareaRepo.save(task.get());
            }
        }else{
            return new ResponseEntity<>("Must assign the note to a task or a project.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Note created.", HttpStatus.OK);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateNote(@PathVariable("id") Long id, @RequestBody Nota note, HttpServletRequest request){
        Optional<Nota> nota = notaRepo.findById(id);
        if(nota.isEmpty()){
            return new ResponseEntity<>("Note not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = nota.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            nota.get().setContenido(note.getContenido());
            notaRepo.save(nota.get());
            return new ResponseEntity<>("Note updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Note not found.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Nota> nota = notaRepo.findById(id);
        if(nota.isEmpty()){
            return new ResponseEntity<>("Note not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = nota.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                notaRepo.deleteById(id);
                return new ResponseEntity<>("Note deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Note not found", HttpStatus.NOT_FOUND);
            }
        }
    }
}