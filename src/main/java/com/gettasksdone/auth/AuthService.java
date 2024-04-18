package com.gettasksdone.auth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Usuario.Rol;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthService {

    // private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
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

    public ResponseEntity<String> sudoRegister(RegisterRequest request){
        List<Usuario> adminUsers = usuarioRepo.findByRol(Rol.ADMINISTRADOR);
        if(!adminUsers.isEmpty()){
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }else{
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Optional<Usuario> usuario = userRepository.findByUsername(request.getUsername());
            if(!usuario.isEmpty()){
                return new ResponseEntity<>("The username already exists.", HttpStatus.BAD_REQUEST);
            }else{
                usuario = userRepository.findByEmail(request.getEmail());
                if(!usuario.isEmpty()){
                    return new ResponseEntity<>("The email already exists.", HttpStatus.BAD_REQUEST);
                }else{
                    //Construye el usuario administrador
                    Usuario user = Usuario.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .email(request.getEmail())
                        .rol(Rol.ADMINISTRADOR)
                        .build();
                    //Se almacena el usuario en la base de datos y se crea su proyecto generico inbox
                    Usuario newUser = userRepository.save(user);
                    Proyecto inbox = Proyecto.builder()
                        .nombre("inbox")
                        .descripcion("inbox")
                        .estado("inbox")
                        .usuario(newUser)
                        .inicio(LocalDateTime.now())
                        .fin(LocalDateTime.parse("9999-12-31 23:59:59", timeFormat))
                        .build();
                    proyectoRepo.save(inbox);
                    //Se procesa su token de autenticacion y la retorna
                    AuthResponse token = AuthResponse
                    .builder()
                    .token(jwtService.getToken(user))
                    .build();
                    return new ResponseEntity<>(token.getToken(), HttpStatus.OK);
                }
            }
        }
    }

    public ResponseEntity<String> register(RegisterRequest request){
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                
                /*
                * Se guarda el usuario nuevo en la BD y se construye un proyecto genérico Inbox asignado a ese usuario
                */
                Usuario newUser = userRepository.save(user);
                Proyecto inbox = Proyecto.builder()
                    .nombre("inbox")
                    .descripcion("inbox")
                    .estado("inbox")
                    .usuario(newUser)
                    .inicio(LocalDateTime.now())
                    .fin(LocalDateTime.parse("9999-12-31 23:59:59", timeFormat))
                    .build();
                proyectoRepo.save(inbox);
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
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                /*
                * Se guarda el usuario nuevo en la BD y se construye un proyecto genérico Inbox asignado a ese usuario
                */
                Usuario newUser = userRepository.save(user);
                Proyecto inbox = Proyecto.builder()
                    .nombre("inbox")
                    .descripcion("inbox")
                    .estado("inbox")
                    .usuario(newUser)
                    .inicio(LocalDateTime.now())
                    .fin(LocalDateTime.parse("9999-12-31 23:59:59", timeFormat))
                    .build();
                proyectoRepo.save(inbox);
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
