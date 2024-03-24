package com.gettasksdone.auth;

import org.springframework.web.bind.annotation.RestController;
import java.util.Collection;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return authService.register(request);

    }

    @GetMapping(value = "/oauth")
    public ResponseEntity<String> oauthManager(HttpServletRequest request,  HttpServletResponse response) {

        //String username = response.getHeader("username");
        String username = (String) request.getSession().getAttribute("username");
        // Collection<String> headers = response.getHeaderNames();
        // Iterator<String> headersIterator = headers.iterator();
        // while(headersIterator.hasNext()){
        //     logger.info("Header: " + headersIterator.next());
        // }
        logger.info("Session ID oauthManager: " + request.getSession().getId());
        logger.info("EL CORREOOOO ES :" + username);
        logger.info("El ...." + request.getSession().getAttributeNames());
        

        //Implementar la lógica de creación del token.
        return authService.manageOAuth(request);
    }
}
