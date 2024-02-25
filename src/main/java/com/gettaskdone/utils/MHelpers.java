package com.gettaskdone.utils;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;

import com.gettasksdone.jwt.JwtService;

import io.micrometer.core.ipc.http.HttpSender.Request;
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

    

}
