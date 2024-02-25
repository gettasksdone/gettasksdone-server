package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<InfoUsuario>> allUsersData(){
		return new ResponseEntity<>(infoUsuarioRepo.findAll(), HttpStatus.OK);
	}
    
    @GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<InfoUsuario> infoUser = infoUsuarioRepo.findById(id);
        if(infoUser.isEmpty()){
            return new ResponseEntity<>("User data not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(infoUser.get(), HttpStatus.OK);
        }
	}

    @PostMapping("/create")
	public ResponseEntity<?> createUserData(@RequestBody InfoUsuario userData) {
        Optional<Usuario> user = usuarioRepo.findById(userData.getUsuario().getId());
        if(user.isEmpty()){
            return new ResponseEntity<>("User does not exist.", HttpStatus.BAD_REQUEST);
        }
        Optional<InfoUsuario> infoUsuario = infoUsuarioRepo.findByUsuario(user.get());
        if(infoUsuario.isPresent()){
            return new ResponseEntity<>("User data already exist for this user.", HttpStatus.BAD_REQUEST);
        }
        userData.setUsuario(user.get());
		return new ResponseEntity<>(infoUsuarioRepo.save(userData), HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
	public ResponseEntity<?> updateUserData(@PathVariable("id") Long id ,@RequestBody InfoUsuario userData) {
        Optional<InfoUsuario> userInfo = infoUsuarioRepo.findById(id);
        if(userInfo.isEmpty()){
            return new ResponseEntity<>("User data does not exist.", HttpStatus.NOT_FOUND);
        }
        userInfo.get().setNombre(userData.getNombre());
        userInfo.get().setTelefono(userData.getTelefono());
        userInfo.get().setPuesto(userData.getPuesto());
        userInfo.get().setDepartamento(userData.getDepartamento());
		return new ResponseEntity<>(infoUsuarioRepo.save(userInfo.get()), HttpStatus.OK);
	}

    @DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteUserData(@PathVariable("id") Long id) {
        if(infoUsuarioRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("User Info not found", HttpStatus.NOT_FOUND);
        }else{
            infoUsuarioRepo.deleteById(id);
            return new ResponseEntity<>("User Info deleted", HttpStatus.OK);
        }
	}
}
