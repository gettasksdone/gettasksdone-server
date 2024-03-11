package com.gettasksdone.controller;

import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.gettasksdone.dto.InfoUsuarioDTO;
import com.gettasksdone.model.InfoUsuario;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.InfoUsuarioRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.InfoUsuarioService;
import com.gettasksdone.utils.MHelpers;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/userData")
@SuppressWarnings("null")
public class UserInfoController {
    @Autowired
    private InfoUsuarioRepository infoUsuarioRepo;
    @Autowired
	private UsuarioRepository usuarioRepo;
    @Autowired
    private InfoUsuarioService infoUsuarioService;

    @GetMapping("/")
    public ResponseEntity<?> dataUser(HttpServletRequest request) {
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        InfoUsuarioDTO infoUsuario = infoUsuarioService.findByUsuario(authedUser);
        if(infoUsuario == null){
            return new ResponseEntity<>("This user does not have additional info created.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(infoUsuario, HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getUserData")
	public ResponseEntity<?> allUsersData(){
		return new ResponseEntity<>(infoUsuarioService.findAll(), HttpStatus.OK);
	}
    
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable("id") Long id){
        InfoUsuarioDTO infoUser = infoUsuarioService.findById(id);
        if(infoUser == null){
            return new ResponseEntity<>("User data not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(infoUser, HttpStatus.OK);
        }
	}

    @PostMapping("/create")
	public ResponseEntity<?> createUserData(@RequestBody InfoUsuario userData, HttpServletRequest request) {
        Optional<Usuario> user = usuarioRepo.findById(userData.getUsuario().getId());
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        if(user.isEmpty()){
            return new ResponseEntity<>("User does not exist.", HttpStatus.BAD_REQUEST);
        }
        Optional<InfoUsuario> infoUsuario = infoUsuarioRepo.findByUsuario(user.get());
        if(infoUsuario.isPresent()){
            return new ResponseEntity<>("User data already exist for this user.", HttpStatus.BAD_REQUEST);
        }
        userData.setUsuario(user.get());
        if(MHelpers.checkAccess(user.get().getId(), authedUser.get())){
            infoUsuarioRepo.save(userData);
            return new ResponseEntity<>("User data created.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User does not exist.", HttpStatus.BAD_REQUEST);
        }
	}

    @PatchMapping("/update/{id}")
	public ResponseEntity<?> updateUserData(@PathVariable("id") Long id ,@RequestBody InfoUsuario userData, HttpServletRequest request) {
        Optional<InfoUsuario> userInfo = infoUsuarioRepo.findById(id);
        if(userInfo.isEmpty()){
            return new ResponseEntity<>("User data does not exist.", HttpStatus.NOT_FOUND);
        }
        Long ownerID = userInfo.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            userInfo.get().setNombre(userData.getNombre());
            userInfo.get().setTelefono(userData.getTelefono());
            userInfo.get().setPuesto(userData.getPuesto());
            userInfo.get().setDepartamento(userData.getDepartamento());
            infoUsuarioRepo.save(userInfo.get());
            return new ResponseEntity<>("User data updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User data does not exist.", HttpStatus.NOT_FOUND);
        }
	}

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
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
