package com.gettasksdone.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gettasksdone.dto.ContextoDTO;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.ContextoRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.ContextoService;
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
@RequestMapping("/context")
@SuppressWarnings("null")
@Tag(name = "Controlador de contextos", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar los contextos de la aplicación.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class ContextoController {

    @Autowired
    private ContextoRepository contextoRepo;
    @Autowired
    private ContextoService contextoService;
    @Autowired
    private UsuarioRepository usuarioRepo;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getContexts")
    @Operation(
        summary = "Obtiene todos los contextos.",
        description = "Se obtienen todos los contextos registrados en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ContextoDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allContexts(){
		return new ResponseEntity<>(contextoService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene los contextos del usuario autenticado.",
        description = "Se obtienen los contextos registrados en la aplicación por el usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ContextoDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> contextsFromUser(HttpServletRequest request) {
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(contextoService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Busca un contexto.",
        description = "Se busca un contexto específico en la aplicación. Solamente el usuario que haya creado el contexto o un administrador podrán verlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del contexto a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Context not found."), mediaType = "string")}, description = "El contexto no existe en la aplicación, o no se tiene permiso para consultarlo."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ContextoDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = context.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(contextoService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Context not found.", HttpStatus.NOT_FOUND);
            }
        }
	}

    @PostMapping("/createContext")
    @Operation(
        summary = "Crea un contexto.",
        description = "Se da de alta un contexto en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del contexto.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Contexto de prueba\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Long.class), mediaType = "string")}, description = "El contexto se ha creado correctamente. Se retorna el identificador del contexto nuevo.")
    })
	public ResponseEntity<?> createContext(@RequestBody Contexto contexto, HttpServletRequest request) {
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        contexto.setUsuario(authedUser);
        Contexto cContexto = contextoRepo.save(contexto);
		return new ResponseEntity<>(cContexto.getId(), HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica un contexto.",
        description = "Se modifica un contexto existente en la aplicación. Solo el usuario que ha creado el contexto o un administrador pueden modificarlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del contexto a modificar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del contexto.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Contexto de prueba\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Context updated."), mediaType = "string")}, description = "El contexto se ha modificado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "Context not found."), mediaType = "string")}, description = "No se ha encontrado el contexto, o no se tiene autorización para modificarlo.")
    })
	public ResponseEntity<?> updateContext(@PathVariable("id") Long id ,@RequestBody Contexto contexto, HttpServletRequest request) {
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = context.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            context.get().setNombre(contexto.getNombre());
            contextoRepo.save(context.get());
            return new ResponseEntity<>("Context updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
        }
	}

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina un contexto.",
        description = "Se borra un contexto de la aplicación. Solo el usuario que ha creado el contexto o un administrador pueden borrarlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del contexto a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Context deleted."), mediaType = "string")}, description = "El contexto se ha eliminado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Context not found."), mediaType = "string")}, description = "No se ha encontrado el contexto, o no se tiene autorización para eliminarlo.")
    })
	public ResponseEntity<String> deleteContext(@PathVariable("id") Long id, HttpServletRequest request) {
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = context.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                contextoRepo.deleteById(id);
                return new ResponseEntity<>("Context deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Context not found", HttpStatus.NOT_FOUND);
            }
        }
	}
}
