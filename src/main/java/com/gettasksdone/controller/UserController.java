package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.gettasksdone.dto.UserDTO;
import com.gettasksdone.jwt.JwtService;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.UsuarioService;
import com.gettasksdone.utils.MHelpers;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/user")
@SuppressWarnings("null")
public class UserController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
	private UsuarioRepository usuarioRepo;
    JwtService jwt = new JwtService();

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/authed")
    public ResponseEntity<Object> dataUser(HttpServletRequest request){
        UserDTO usuario = usuarioService.findById(MHelpers.getIdToken(request));
        if(usuario == null){
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping(value = "/users")
	public ResponseEntity<?> allUsers(){
        //return new ResponseEntity<>(usuarioRepo.findAll(), HttpStatus.OK); //Devuelve la informacion COMPLETA de la BD
        return ResponseEntity.ok(this.usuarioService.findAll()); //Devuelve solamente los valores creados en UsuarioDTO
	}

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        UserDTO user = usuarioService.findById(id);
        if(user == null){
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody Usuario usuario, HttpServletRequest request){
        Optional<Usuario> user = usuarioRepo.findById(id), authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        if(!user.isEmpty() && MHelpers.checkAccess(user.get().getId(), authedUser.get())){
            user.get().setEmail(usuario.getEmail());
            user.get().setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepo.save(user.get());
            return new ResponseEntity<>("Update completed.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
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
