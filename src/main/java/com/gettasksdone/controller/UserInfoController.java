package com.gettasksdone.controller;

import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.gettasksdone.dto.InfoUsuarioDTO;
import com.gettasksdone.model.InfoUsuario;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.InfoUsuarioRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.InfoUsuarioService;
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

@RestController
@RequestMapping("/userData")
@SuppressWarnings("null")
@Tag(name = "Controlador de información adicional de usuarios", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar la información adicional de los usuarios registrados en la aplicación.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class UserInfoController {
    @Autowired
    private InfoUsuarioRepository infoUsuarioRepo;
    @Autowired
	private UsuarioRepository usuarioRepo;
    @Autowired
    private InfoUsuarioService infoUsuarioService;

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene la información adicional del usuario autenticado.",
        description = "Se obtiene la información adicional del usuario autenticado en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = InfoUsuarioDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> dataUser(HttpServletRequest request) {
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        InfoUsuarioDTO infoUsuario = infoUsuarioService.findByUsuario(authedUser);
        if(infoUsuario == null){
            return new ResponseEntity<>("This user does not have additional info created.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(infoUsuario, HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getUserData")
    @Operation(
        summary = "Obtiene la información adicional de todos los usuarios registrados.",
        description = "Se obtiene la información adicional de todos los usuarios registrados en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = InfoUsuarioDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allUsersData(){
		return new ResponseEntity<>(infoUsuarioService.findAll(), HttpStatus.OK);
	}
    
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/{id}")
    @Operation(
        summary = "Busca un nodo de información adicional.",
        description = "Se busca un nodo de información adicional específico en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del nodo de información a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "User data not found."), mediaType = "string")}, description = "El nodo de información adicional no existe en la aplicación."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = InfoUsuarioDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> findById(@PathVariable("id") Long id){
        InfoUsuarioDTO infoUser = infoUsuarioService.findById(id);
        if(infoUser == null){
            return new ResponseEntity<>("User data not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(infoUser, HttpStatus.OK);
        }
	}

    @PostMapping("/create")
    @Operation(
        summary = "Crea un nodo de información adicional.",
        description = "Se da de alta un nodo de información adicional en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del nodo de información adicional.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Pedro\", \"telefono\": \"123456789\", \"puesto\": \"Reclutador\", \"departamento\": \"Recursos Humanos\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "User data created."), mediaType = "string")}, description = "El nodo de información adicional se ha creado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "User data already exist for this user."), mediaType = "string")}, description = "El usuario que ha intentado crear el nodo de información adicional ya tiene un nodo propio.")
    })
	public ResponseEntity<?> createUserData(@RequestBody InfoUsuario userData, HttpServletRequest request) {
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        Optional<InfoUsuario> infoUsuario = infoUsuarioRepo.findByUsuario(authedUser.get());
        if(infoUsuario.isPresent()){
            return new ResponseEntity<>("User data already exist for this user.", HttpStatus.BAD_REQUEST);
        }
        userData.setUsuario(authedUser.get());
        infoUsuarioRepo.save(userData);
        return new ResponseEntity<>("User data created.", HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica un nodo de información adicional.",
        description = "Se modifica un nodo de información adicional existente en la aplicación. Solo el usuario que ha creado el nodo de información adicional o un administrador pueden modificarlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del nodo de información adicional a modificar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del nodo de información adicional.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Pedro\", \"telefono\": \"123456789\", \"puesto\": \"Reclutador\", \"departamento\": \"Recursos Humanos\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "User data updated."), mediaType = "string")}, description = "El nodo de información adicional se ha modificado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "User data not found."), mediaType = "string")}, description = "No se ha encontrado el nodo de información adicional, o no se tiene autorización para modificarlo.")
    })
	public ResponseEntity<?> updateUserData(@PathVariable("id") Long id ,@RequestBody InfoUsuario userData, HttpServletRequest request) {
        Optional<InfoUsuario> userInfo = infoUsuarioRepo.findById(id);
        if(userInfo.isEmpty()){
            return new ResponseEntity<>("User data does not exist.", HttpStatus.NOT_FOUND);
        }
        Long ownerID = userInfo.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            userInfo.get().setNombre(userData.getNombre());
            userInfo.get().setTelefono(userData.getTelefono());
            userInfo.get().setPuesto(userData.getPuesto());
            userInfo.get().setDepartamento(userData.getDepartamento());
            infoUsuarioRepo.save(userInfo.get());
            return new ResponseEntity<>("User data updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User data does not exist.", HttpStatus.NOT_FOUND);
        }
	}

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina un nodo de información adicional.",
        description = "Se borra un nodo de información adicional de la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del nodo de información adicional a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "User data deleted."), mediaType = "string")}, description = "El nodo de información adicional se ha eliminado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "User data not found."), mediaType = "string")}, description = "No se ha encontrado el nodo de información adicional.")
    })
	public ResponseEntity<String> deleteUserData(@PathVariable("id") Long id) {
        if(infoUsuarioRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("User Info not found", HttpStatus.NOT_FOUND);
        }else{
            infoUsuarioRepo.deleteById(id);
            return new ResponseEntity<>("User Info deleted", HttpStatus.OK);
        }
	}
}
