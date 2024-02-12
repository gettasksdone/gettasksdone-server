package com.gettasksdone.auth;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
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


    
    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return authService.register(request);

    }
}
