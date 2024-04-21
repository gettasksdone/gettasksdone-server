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
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.ProyectoService;
import com.gettasksdone.utils.MHelpers;
import jakarta.servlet.http.HttpServletRequest;
import com.gettasksdone.repository.EtiquetaRepository;

@RestController
@RequestMapping("/project")
@SuppressWarnings("null")
public class ProjectController {
    
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/authed")
    public ResponseEntity<?> projectsFromUser(HttpServletRequest request) {
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(proyectoService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }
    

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getProjects")
	public ResponseEntity<?> allProjects(){
		return new ResponseEntity<>(proyectoService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = project.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(proyectoService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Project not found.", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody Proyecto project, HttpServletRequest request){
        Optional<Usuario> user = usuarioRepo.findById(MHelpers.getIdToken(request));
        if(project.getNombre().toLowerCase().equals("inbox")){
            return new ResponseEntity<>("Inbox is a reservated project name.", HttpStatus.BAD_REQUEST);
        }
        project.setUsuario(user.get());
        Proyecto proyecto = proyectoRepo.save(project);
        return new ResponseEntity<>(proyecto.getId(), HttpStatus.OK);
    }
    
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateProject(@PathVariable("id") Long id, @RequestBody Proyecto project, HttpServletRequest request){
        Optional<Proyecto> proyecto = proyectoRepo.findById(id);
        if(proyecto.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = proyecto.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            if(proyecto.get().getNombre().equals("inbox")){
                return new ResponseEntity<>("Inbox project is protected from deletion or modification", HttpStatus.BAD_REQUEST);
            }
            proyecto.get().setNombre(project.getNombre());
            proyecto.get().setDescripcion(project.getDescripcion());
            proyecto.get().setEstado(project.getEstado());
            proyecto.get().setInicio(project.getInicio());
            proyecto.get().setFin(project.getFin());
            proyectoRepo.save(proyecto.get());
            return new ResponseEntity<>("Project updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = project.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                if(project.get().getNombre().equals("inbox")){
                    return new ResponseEntity<>("Inbox project is protected from deletion or modification", HttpStatus.BAD_REQUEST);
                }
                proyectoRepo.deleteById(id);
                return new ResponseEntity<>("Project deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PatchMapping("/addTag/{id}")
    public ResponseEntity<?> addTagToProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found", HttpStatus.BAD_REQUEST);
            }else{
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                Long tagOwner = tag.get().getUsuario().getId();
                if(MHelpers.checkAccess(tagOwner, authedUser)){
                    Long ownerID = project.get().getUsuario().getId();
                    if(MHelpers.checkAccess(ownerID, authedUser)){
                        List<Etiqueta> tagList = project.get().getEtiquetas();
                        if(tagList.contains(tag.get())){
                            return new ResponseEntity<>("Tag already on the project.", HttpStatus.BAD_REQUEST);
                        }
                        tagList.add(tag.get());
                        project.get().setEtiquetas(tagList);
                        proyectoRepo.save(project.get());
                        return new ResponseEntity<>("Tag added to project.", HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    public ResponseEntity<?> delTagFromProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                Long tagOwner = tag.get().getUsuario().getId();
                if(MHelpers.checkAccess(tagOwner, authedUser)){
                    Long ownerID = project.get().getUsuario().getId();
                    if(MHelpers.checkAccess(ownerID, authedUser)){
                        List<Etiqueta> tagList = project.get().getEtiquetas();
                        if(!tagList.contains(tag.get())){
                            return new ResponseEntity<>("Tag not present in the project.", HttpStatus.BAD_REQUEST);
                        }
                        tagList.remove(tag.get());
                        project.get().setEtiquetas(tagList);
                        proyectoRepo.save(project.get());
                        return new ResponseEntity<>("Tag deleted from project.", HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }
}
