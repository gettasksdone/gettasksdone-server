package com.gettasksdone.configuration;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Usuario.Rol;
import com.gettasksdone.repository.UsuarioRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SuccessHandler implements AuthenticationSuccessHandler{

    private static final Logger logger = LoggerFactory.getLogger(SuccessHandler.class);
    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

            if(authentication.getPrincipal() instanceof DefaultOAuth2User){
                DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
                String username = userDetails.getAttribute("email");
                logger.info("USER EMAIL:"+username);
                Optional<Usuario> usuario = userRepository.findByUsername(username);
                if(usuario.isEmpty()){
                    usuario = userRepository.findByEmail(username);
                    if(usuario.isEmpty()){
                            Usuario user = Usuario.builder()
                                .username(username)
                                .password(passwordEncoder.encode(username))
                                .email(username)
                                .rol(Rol.USUARIO)
                                .build();
                            
                            userRepository.save(user);
                    }
                }
            }
            new DefaultRedirectStrategy().sendRedirect(request, response, "/");
    }
}
