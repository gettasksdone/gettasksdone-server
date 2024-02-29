package com.gettasksdone.utils;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import com.gettasksdone.jwt.JwtService;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Usuario.Rol;
import jakarta.servlet.http.HttpServletRequest;

public class MHelpers {

    public static ModelMapper modelMapper() {

        return new ModelMapper();
    }

    public static Long getIdToken(HttpServletRequest request){
        JwtService jwt = new JwtService();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        token = token.substring(7);
        Long id = jwt.getIdFromToken(token);
        return id;
    }

    public static boolean checkAccess(Long ownerID, Usuario authedUser){
        return ownerID == authedUser.getId() || authedUser.getRol() == Rol.ADMINISTRADOR;
    }
}
