package com.gettasksdone.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.EtiquetaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.EtiquetaService;
import com.gettasksdone.utils.MHelpers;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tag")
@SuppressWarnings("null")
public class EtiquetaController {

    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private EtiquetaService etiquetaService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getTags")
	public ResponseEntity<?> allTags(){
		return new ResponseEntity<>(etiquetaService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/authed")
    public ResponseEntity<?> tagsFromUser(HttpServletRequest request){
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(etiquetaService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = tag.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(etiquetaService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Tag not found.", HttpStatus.NOT_FOUND);
            }
        }
	}

    @PostMapping("/createTag")
	public ResponseEntity<?> createTag(@RequestBody Etiqueta etiqueta, HttpServletRequest request) {
        Usuario user = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        etiqueta.setUsuario(user);
        etiquetaRepo.save(etiqueta);
		return new ResponseEntity<>(etiqueta.getId(), HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
	public ResponseEntity<?> updateTag(@PathVariable("id") Long id ,@RequestBody Etiqueta etiqueta, HttpServletRequest request) {
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = tag.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            tag.get().setNombre(etiqueta.getNombre());
            etiquetaRepo.save(tag.get());
            return new ResponseEntity<>("Tag updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
        }
	}

    @DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteTag(@PathVariable("id") Long id, HttpServletRequest request) {
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = tag.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                etiquetaRepo.deleteById(id);
                return new ResponseEntity<>("Tag deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Tag not found", HttpStatus.NOT_FOUND);
            }
        }
	}
}
