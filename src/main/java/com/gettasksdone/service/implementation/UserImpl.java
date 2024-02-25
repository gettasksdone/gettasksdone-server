package com.gettasksdone.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import com.gettaskdone.utils.MHelpers;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.repository.dto.UserDTO;
import com.gettasksdone.service.UsuarioService;


@Component
public class UserImpl implements UsuarioService
{

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

        // return users.stream()
        //                 .map(this::convertToUsersDTO)
        //                 .collect(Collectors.toList());

        
    }

    @Override
    public UserDTO findByUsername(String username) {


    Optional<Usuario> user = this.userRepository.findByUsername(username);

    if(!user.isPresent()){
        return null;
    }
        
        return MHelpers.modelMapper().map(user.get(), UserDTO.class);
    }


    private UserDTO convertToUsersDTO( final Usuario user ){
        return MHelpers.modelMapper().map(user, UserDTO.class);
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
