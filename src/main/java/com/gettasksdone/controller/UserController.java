package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.gettaskdone.utils.MHelpers;
import com.gettasksdone.jwt.JwtService;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Usuario.Rol;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
	private UsuarioRepository usuarioRepo;
    JwtService jwt = new JwtService();

    
    @PreAuthorize("hasAuthority('USUARIO')")
    @GetMapping("/data")
    public ResponseEntity<Object> dataUser(HttpServletRequest request){
        return ResponseEntity.ok(this.usuarioService.findById(MHelpers.getIdToken(request)));
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping(value = "/users")
	public ResponseEntity<?> allUsers(HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        token = token.substring(7);

        String authedUsername = jwt.getUsernameFromToken(token);
        Optional<Usuario> authedUser = usuarioRepo.findByUsername(authedUsername);
        if(authedUser.isEmpty()){
            return new ResponseEntity<>("Username does not exist.", HttpStatus.FORBIDDEN);
        }else{
            if(authedUser.get().getRol() == Rol.ADMINISTRADOR){
                return new ResponseEntity<>(usuarioRepo.findAll(), HttpStatus.OK);
                //return ResponseEntity.ok(this.usuarioService.findAll());
            }else{
                return new ResponseEntity<>("User not authorized.", HttpStatus.FORBIDDEN);
            }
        }
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
        }else{
            user.get().setEmail(usuario.getEmail());
            user.get().setPassword(usuario.getPassword());
            return new ResponseEntity<>(usuarioRepo.save(user.get()), HttpStatus.OK);
        }
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
