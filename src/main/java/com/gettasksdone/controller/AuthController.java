package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.RestController;

import com.gettasksdone.model.LoginRequest;
import com.gettasksdone.model.RegisterRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping(value = "/login", consumes = "application/json")
    public String login(@RequestBody LoginRequest request) {

        logger.info("HOLA DESDE EL LOGIN");

        return "Login";
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public String register(@RequestBody RegisterRequest request) {

        logger.info("HOLA DESDE EL REGISTER");

        return "Register";
    }
}
