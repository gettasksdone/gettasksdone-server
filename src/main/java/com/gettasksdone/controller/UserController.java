package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.UsuarioRepository;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
	private UsuarioRepository usuarioRepo;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/users")
	public ResponseEntity<List<Usuario>> allUsers(){
        Logger logger = LoggerFactory.getLogger(UserController.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        logger.debug(username);
		return new ResponseEntity<>(usuarioRepo.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<Usuario> user = usuarioRepo.findById(id);
        if(user.isEmpty()){
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody Usuario usuario){
        Optional<Usuario> user = usuarioRepo.findById(id);
        if(user.isEmpty()){
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        user.get().setEmail(usuario.getEmail());
        user.get().setPassword(usuario.getPassword());
        return new ResponseEntity<>(usuarioRepo.save(user.get()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id){
        if(usuarioRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }else{
            usuarioRepo.deleteById(id);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        }
    }
}
