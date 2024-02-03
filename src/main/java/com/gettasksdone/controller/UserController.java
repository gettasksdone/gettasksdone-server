package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/users")
	public List<Usuario> allUsers(){
		return usuarioRepo.findAll();
	}

    @PostMapping("/create")
	public Usuario createUser(@RequestBody Usuario usuario) {
        usuario.setRol(Usuario.Rol.USUARIO);
		return usuarioRepo.save(usuario);
	}

    @GetMapping("/{id}")
    public Optional<Usuario> findById(@PathVariable("id") Long id){
        return usuarioRepo.findById(id);
    }

    @PatchMapping("/update/{id}")
    public Usuario updateUser(@PathVariable("id") Long id, @RequestBody Usuario usuario){
        Optional<Usuario> user = usuarioRepo.findById(id);
        if(user.isEmpty()){
            return null;
        }
        user.get().setEmail(usuario.getEmail());
        user.get().setPassword(usuario.getPassword());
        return usuarioRepo.save(user.get());
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        if(usuarioRepo.findById(id).isEmpty()){
            return "User not found";
        }else{
            usuarioRepo.deleteById(id);
            return "User deleted";
        }
    }
}
