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
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.repository.EtiquetaRepository;

@RestController
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping("/getProjects")
	public ResponseEntity<List<Proyecto>> allProjects(){
		return new ResponseEntity<>(proyectoRepo.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(project.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody Proyecto project){
        Optional<Usuario> user = usuarioRepo.findById(project.getUsuario().getId());
        if(user.isEmpty()){
            return new ResponseEntity<>("User not found.", HttpStatus.BAD_REQUEST);
        }else{
            project.setUsuario(user.get());
            return new ResponseEntity<>(proyectoRepo.save(project), HttpStatus.OK);
        }
    }
    
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateProject(@PathVariable("id") Long id, @RequestBody Proyecto project){
        Optional<Proyecto> proyecto = proyectoRepo.findById(id);
        if(proyecto.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }
        proyecto.get().setNombre(project.getNombre());
        proyecto.get().setDescripcion(project.getDescripcion());
        proyecto.get().setEstado(project.getEstado());
        proyecto.get().setInicio(project.getInicio());
        proyecto.get().setFin(project.getFin());
        return new ResponseEntity<>(proyectoRepo.save(proyecto.get()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id){
        if(proyectoRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
        }else{
            proyectoRepo.deleteById(id);
            return new ResponseEntity<>("Project deleted", HttpStatus.OK);
        }
    }

    @PatchMapping("/addTag/{id}")
    public ResponseEntity<?> addTagToProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found", HttpStatus.BAD_REQUEST);
            }else{
                List<Etiqueta> tagList = project.get().getEtiquetas();
                if(tagList.contains(tag.get())){
                    return new ResponseEntity<>("Tag already on the project.", HttpStatus.BAD_REQUEST);
                }
                tagList.add(tag.get());
                project.get().setEtiquetas(tagList);
                return new ResponseEntity<>(proyectoRepo.save(project.get()), HttpStatus.OK);
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    public ResponseEntity<?> delTagFromProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                List<Etiqueta> tagList = project.get().getEtiquetas();
                if(!tagList.contains(tag.get())){
                    return new ResponseEntity<>("Tag not present in the project.", HttpStatus.BAD_REQUEST);
                }
                tagList.remove(tag.get());
                project.get().setEtiquetas(tagList);
                return new ResponseEntity<>(proyectoRepo.save(project.get()), HttpStatus.OK);
            }
        }
    }
}
