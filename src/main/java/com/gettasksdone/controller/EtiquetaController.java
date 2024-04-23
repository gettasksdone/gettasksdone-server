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
import com.gettasksdone.dto.EtiquetaDTO;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.EtiquetaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.EtiquetaService;
import com.gettasksdone.utils.MHelpers;
import jakarta.servlet.http.HttpServletRequest;
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

@RestController
@RequestMapping("/tag")
@SuppressWarnings("null")
@Tag(name = "Controlador de etiquetas", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar las etiquetas de la aplicación.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class EtiquetaController {

    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private EtiquetaService etiquetaService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getTags")
    @Operation(
        summary = "Obtiene todas las etiquetas.",
        description = "Se obtienen todas las etiquetas registradas en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = EtiquetaDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allTags(){
		return new ResponseEntity<>(etiquetaService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene las etiquetas del usuario autenticado.",
        description = "Se obtienen las etiquetas registradas en la aplicación por el usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = EtiquetaDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> tagsFromUser(HttpServletRequest request){
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(etiquetaService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Busca una etiqueta.",
        description = "Se busca una etiqueta específica en la aplicación. Solamente el usuario que haya creado la etiqueta o un administrador podrán verla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la etiqueta a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Tag not found."), mediaType = "string")}, description = "La etiqueta no existe en la aplicación, o no se tiene permiso para consultarla."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = EtiquetaDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = tag.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(etiquetaService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Tag not found.", HttpStatus.NOT_FOUND);
            }
        }
	}

    @PostMapping("/createTag")
    @Operation(
        summary = "Crea una etiqueta.",
        description = "Se da de alta una etiqueta en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada de la etiqueta.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Etiqueta de prueba\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Long.class), mediaType = "string")}, description = "La etiqueta se ha creado correctamente. Se retorna el identificador de la etiqueta nueva.")
    })
	public ResponseEntity<?> createTag(@RequestBody Etiqueta etiqueta, HttpServletRequest request) {
        Usuario user = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        etiqueta.setUsuario(user);
        Etiqueta cEtiqueta = etiquetaRepo.save(etiqueta);
		return new ResponseEntity<>(cEtiqueta.getId(), HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica una etiqueta.",
        description = "Se modifica una etiqueta existente en la aplicación. Solo el usuario que ha creado la etiqueta o un administrador pueden modificarla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la etiqueta a modificar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada de la etiqueta.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Etiqueta de prueba\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Tag updated."), mediaType = "string")}, description = "La etiqueta se ha modificado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "Tag not found."), mediaType = "string")}, description = "No se ha encontrado la etiqueta, o no se tiene autorización para modificarla.")
    })
	public ResponseEntity<?> updateTag(@PathVariable("id") Long id ,@RequestBody Etiqueta etiqueta, HttpServletRequest request) {
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = tag.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            tag.get().setNombre(etiqueta.getNombre());
            etiquetaRepo.save(tag.get());
            return new ResponseEntity<>("Tag updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
        }
	}

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina una etiqueta.",
        description = "Se borra una etiqueta de la aplicación. Solo el usuario que ha creado la etiqueta o un administrador pueden borrarla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la etiqueta a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Tag deleted."), mediaType = "string")}, description = "La etiqueta se ha eliminado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Tag not found."), mediaType = "string")}, description = "No se ha encontrado la etiqueta, o no se tiene autorización para eliminarla.")
    })
	public ResponseEntity<String> deleteTag(@PathVariable("id") Long id, HttpServletRequest request) {
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = tag.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                etiquetaRepo.deleteById(id);
                return new ResponseEntity<>("Tag deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Tag not found", HttpStatus.NOT_FOUND);
            }
        }
	}
}
