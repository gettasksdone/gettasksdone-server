package com.gettasksdone.service;

import java.util.List;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Service;

import com.gettasksdone.repository.dto.UserDTO;

@Service
public interface UsuarioService {

    
    List<UserDTO> findAll();

    UserDTO findByUsername(String username);

    UserDTO findById(Long id);

}
