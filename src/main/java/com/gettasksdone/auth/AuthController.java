package com.gettasksdone.auth;

import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }


    @GetMapping(value = "/prueba")
    public String prueba(Model model) {
        return "login";
    }


    
    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return authService.register(request);

    }
}
