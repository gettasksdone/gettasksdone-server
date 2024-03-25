package com.gettasksdone.auth;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gettasksdone.jwt.JwtService;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Usuario.Rol;
import com.gettasksdone.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthService {

    // private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ResponseEntity<String> login(LoginRequest request){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        }catch(BadCredentialsException ex){
            return new ResponseEntity<>("Invalid credentials.", HttpStatus.FORBIDDEN);
        }catch(DisabledException ex){
            return new ResponseEntity<>("This account has been disabled. Contact your administrator.", HttpStatus.FORBIDDEN);
        }catch(LockedException ex){
            return new ResponseEntity<>("This account has been locked. Contact your administrator.", HttpStatus.FORBIDDEN);
        }
        UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        String token = jwtService.getToken(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public ResponseEntity<String> register(RegisterRequest request){

        Optional<Usuario> usuario = userRepository.findByUsername(request.getUsername());
        if(usuario.isEmpty()){
            usuario = userRepository.findByEmail(request.getEmail());
            if(usuario.isEmpty()){
                Usuario user = Usuario.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .rol(Rol.USUARIO)
                    .build();
                
                userRepository.save(user);
                /**
                 * 
                 * Construir la respuesta :
                 * 
                 * Si el usuario es válido y no existe mandamos el token
                 * Si existe mandamos un mensaje de error:
                 * 
                */
                AuthResponse token = AuthResponse
                .builder()
                .token(jwtService.getToken(user))
                .build();
                return new ResponseEntity<>(token.getToken(), HttpStatus.OK);
                
            }else{
                return new ResponseEntity<>("The email already exists.", HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>("The username already exists.", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> manageOAuth(HttpServletRequest request){
        String username = (String) request.getSession().getAttribute("username");
        AuthResponse tokenR;
        // logger.info("USER EMAIL: "+ username);
        Optional<Usuario> usuario = userRepository.findByUsername(username);
        if(usuario.isEmpty()){
            usuario = userRepository.findByEmail(username);
            if(usuario.isEmpty()){ //Nuevo usuario para dar de alta por SSO (Todos sus campos seran el email)
                Usuario user = Usuario.builder()
                    .username(username)
                    .password(passwordEncoder.encode(username))
                    .email(username)
                    .rol(Rol.USUARIO)
                    .build();
                userRepository.save(user);
                tokenR = AuthResponse.builder()
                    .token(jwtService.getToken(user))
                    .build();
            }else{ //Dado de alta previamente de forma "normal"
                tokenR = AuthResponse.builder()
                .token(jwtService.getToken(usuario.get()))
                .build();
            }
        }else{ //Dado de alta previamente por SSO
            tokenR = AuthResponse.builder()
            .token(jwtService.getToken(usuario.get()))
            .build();
        }
        return new ResponseEntity<>(tokenR.getToken(), HttpStatus.OK);
    }

    public String encodePassword(String clearPassword){
        return passwordEncoder.encode(clearPassword);
    }
}
