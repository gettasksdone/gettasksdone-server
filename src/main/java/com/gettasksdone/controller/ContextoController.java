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
import com.gettasksdone.model.Contexto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.ContextoRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.ContextoService;
import com.gettasksdone.utils.MHelpers;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/context")
public class ContextoController {

    @Autowired
    private ContextoRepository contextoRepo;
    @Autowired
    private ContextoService contextoService;
    @Autowired
    private UsuarioRepository usuarioRepo;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getContexts")
	public ResponseEntity<?> allContexts(){
		return new ResponseEntity<>(contextoService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = context.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(contextoService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Context not found.", HttpStatus.NOT_FOUND);
            }
        }
	}

    @PostMapping("/createContext")
	public ResponseEntity<?> createContext(@RequestBody Contexto contexto, HttpServletRequest request) {
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        contexto.setUsuario(authedUser);
        contextoRepo.save(contexto);
		return new ResponseEntity<>("Context created.", HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
	public ResponseEntity<?> updateContext(@PathVariable("id") Long id ,@RequestBody Contexto contexto, HttpServletRequest request) {
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = context.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            context.get().setNombre(contexto.getNombre());
            contextoRepo.save(context.get());
            return new ResponseEntity<>("Context updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
        }
	}

    @DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteContext(@PathVariable("id") Long id, HttpServletRequest request) {
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = context.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                contextoRepo.deleteById(id);
                return new ResponseEntity<>("Context deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Context not found", HttpStatus.NOT_FOUND);
            }
        }
	}
}
