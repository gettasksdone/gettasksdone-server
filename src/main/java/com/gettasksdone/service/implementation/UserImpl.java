package com.gettasksdone.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gettasksdone.dto.UserDTO;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.UsuarioService;
import com.gettasksdone.utils.MHelpers;

@Component
public class UserImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository userRepository;
    
    @Override
    public List<UserDTO> findAll() {
        List<UserDTO> dto = new ArrayList<>();
        List<Usuario> users = this.userRepository.findAll();
        for(Usuario user : users){
            UserDTO userDTO = MHelpers.modelMapper().map(user, UserDTO.class);
            dto.add(userDTO);
        }
        return dto;
    }

    @Override
    public UserDTO findByUsername(String username) {
        Optional<Usuario> user = this.userRepository.findByUsername(username);
        if(!user.isPresent()){
            return null;
        }
        return MHelpers.modelMapper().map(user.get(), UserDTO.class);
    }

    @Override
    public UserDTO findById(Long id) {
        Optional<Usuario> user = this.userRepository.findById(id);
        if(!user.isPresent()){
            return null;
        }
        return MHelpers.modelMapper().map(user.get(), UserDTO.class);
    }

}