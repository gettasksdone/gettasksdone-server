package com.gettasksdone.auth;

import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping(value = "/sudoRegister", consumes = "application/json")
    public ResponseEntity<String> sudoRegister(@RequestBody RegisterRequest request) { //Funciona SOLO si no hay otro administrador en el sistema
        return authService.sudoRegister(request);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping(value = "/oauth")
    public ResponseEntity<String> oauthManager(HttpServletRequest request,  HttpServletResponse response) {
        return authService.manageOAuth(request);
    }
}
