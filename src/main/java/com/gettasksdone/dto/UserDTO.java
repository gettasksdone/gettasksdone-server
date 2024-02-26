package com.gettasksdone.dto;

import com.gettasksdone.model.Usuario.Rol;
import lombok.Data;

@Data
public class UserDTO{
    String username;
    private String email;
    private Rol rol;
}
