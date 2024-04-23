package com.gettasksdone.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.gettasksdone.dto.UserDTO;
import com.gettasksdone.jwt.JwtService;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.UsuarioService;
import com.gettasksdone.utils.MHelpers;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/user")
@SuppressWarnings("null")
@Tag(name = "Controlador de acceso de usuarios", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar las credenciales de acceso de los usuarios de la aplicación junto el controlador de autorización.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class UserController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
	private UsuarioRepository usuarioRepo;
    JwtService jwt = new JwtService();

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene las credenciales del usuario autenticado.",
        description = "Se obtienen las credenciales del usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<Object> dataUser(HttpServletRequest request){
        UserDTO usuario = usuarioService.findById(MHelpers.getIdToken(request));
        if(usuario == null){
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping(value = "/users")
    @Operation(
        summary = "Obtiene las credenciales de todos los usuarios registrados en el sistema.",
        description = "Se obtienen todas las credenciales de los usuarios registrados en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allUsers(){
        //return new ResponseEntity<>(usuarioRepo.findAll(), HttpStatus.OK); //Devuelve la informacion COMPLETA de la BD
        return ResponseEntity.ok(this.usuarioService.findAll()); //Devuelve solamente los valores creados en UsuarioDTO
	}

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/{id}")
    @Operation(
        summary = "Busca las credenciales de un usuario.",
        description = "Se buscan las credenciales de acceso de un usuario específico en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del usuario a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "User not found."), mediaType = "string")}, description = "El usuario no existe en la aplicación."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        UserDTO user = usuarioService.findById(id);
        if(user == null){
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica las credenciales de un usuario.",
        description = "Se modifican las credenciales de acceso de un usuario existente en la aplicación. Solo el usuario o un administrador pueden modificar esta información.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del usuario a modificar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del usuario.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"email\": \"user@gettasksdone.com\", \"password\": \"newPassword\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Update completed."), mediaType = "string")}, description = "Las credenciales del usuario se han modificado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "User not found."), mediaType = "string")}, description = "No se ha encontrado el usuario, o no se tiene autorización para modificarlo.")
    })
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody Usuario usuario, HttpServletRequest request){
        Optional<Usuario> user = usuarioRepo.findById(id), authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        if(!user.isEmpty() && MHelpers.checkAccess(user.get().getId(), authedUser.get())){
            user.get().setEmail(usuario.getEmail());
            user.get().setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepo.save(user.get());
            return new ResponseEntity<>("Update completed.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina un usuario.",
        description = "Se da de baja a un usuario de la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del usuario a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "User deleted."), mediaType = "string")}, description = "El usuario se ha eliminado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "User not found."), mediaType = "string")}, description = "No se ha encontrado el usuario.")
    })
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id){
        if(usuarioRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }else{
            usuarioRepo.deleteById(id);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        }
    }
}
