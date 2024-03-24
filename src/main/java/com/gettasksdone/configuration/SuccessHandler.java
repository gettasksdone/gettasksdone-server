package com.gettasksdone.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.gettasksdone.auth.AuthResponse;
import com.gettasksdone.jwt.JwtService;
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
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            
            //mostra por logs
            logger.info("Ha entrado al authentication\n");
            // logger.info("INICIO DE PARAMETROS DE ENTRADA");
            // logger.info("Request: " + request.getParts());
            // logger.info("Request: " + request.getCookies());
            // logger.info("Request: " + request.getRequestedSessionId());
            // logger.info("Response Status: " + response.getStatus());
            // logger.info("FIN DE PARAMETROS DE ENTRADA");
            logger.info("Session ID onAuthenticationSuccess: " + request.getSession().getId());
 

            AuthResponse token = null;
            if(authentication.getPrincipal() instanceof DefaultOAuth2User){
                DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
                String username = userDetails.getAttribute("email");
                logger.info("USER EMAIL:"+username);
                response.addHeader("username",username);
                request.getSession().setAttribute("username", username);
            //     Optional<Usuario> usuario = userRepository.findByUsername(username);
            //     if(usuario.isEmpty()){
            //         usuario = userRepository.findByEmail(username);
            //         if(usuario.isEmpty()){
            //                 Usuario user = Usuario.builder()
            //                     .username(username)
            //                     .password(passwordEncoder.encode(username))
            //                     .email(username)
            //                     .rol(Rol.USUARIO)
            //                     .build();
                            
            //                 //Un nuevo usuario
            //                 userRepository.save(user);
            //                 token = AuthResponse
            //                 .builder()
            //                 .token(jwtService.getToken(user))
            //                 .build();
            //         }else{ //Dado de alta previamente de forma "normal"
            //             token = AuthResponse
            //             .builder()
            //             .token(jwtService.getToken(usuario.get()))
            //             .build();
            //         }
            //     }else{ //Dado de alta previamente por SSO
            //         token = AuthResponse
            //         .builder()
            //         .token(jwtService.getToken(usuario.get()))
            //         .build();
            //     }
            // }
            }
            new DefaultRedirectStrategy().sendRedirect(request, response, "/auth/oauth");
    }
}
