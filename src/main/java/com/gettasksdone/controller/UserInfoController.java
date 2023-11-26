package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.gettasksdone.model.InfoUsuario;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.InfoUsuarioRepository;
import com.gettasksdone.repository.UsuarioRepository;

@RestController
@RequestMapping("/userData")
public class UserInfoController {
    @Autowired
    private InfoUsuarioRepository infoUsuarioRepo;
    @Autowired
	private UsuarioRepository usuarioRepo;

    @GetMapping("/getUserData")
	public List<InfoUsuario> allUsersData(){
		return infoUsuarioRepo.findAll();
	}
    
    @GetMapping("/{id}")
	public Optional<InfoUsuario> findById(@PathVariable("id") Long id){
        return infoUsuarioRepo.findById(id);
	}

    @PostMapping("/create")
	public InfoUsuario createUserData(@RequestBody InfoUsuario userData) {
        Optional<Usuario> user = usuarioRepo.findById(userData.getUsuario().getId());
        if(user.isEmpty()){
            return null;
        }
        Optional<InfoUsuario> infoUsuario = infoUsuarioRepo.findByUsuario(user.get());
        if(infoUsuario.isPresent()){
            return null;
        }
        userData.setUsuario(user.get());
		return infoUsuarioRepo.save(userData);
	}

    @PatchMapping("/update/{id}")
	public InfoUsuario updateUserData(@PathVariable("id") Long id ,@RequestBody InfoUsuario userData) {
        Optional<InfoUsuario> userInfo = infoUsuarioRepo.findById(id);
        if(userInfo.isEmpty()){
            return null;
        }
        userInfo.get().setNombre(userData.getNombre());
        userInfo.get().setTelefono(userData.getTelefono());
        userInfo.get().setPuesto(userData.getPuesto());
        userInfo.get().setDepartamento(userData.getDepartamento());
		return infoUsuarioRepo.save(userInfo.get());
	}

    @DeleteMapping("/delete/{id}")
	public String deleteUserData(@PathVariable("id") Long id) {
        if(infoUsuarioRepo.findById(id).isEmpty()){
            return "User Info not found";
        }else{
            infoUsuarioRepo.deleteById(id);
            return "User Info deleted";
        }
	}
}
