package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

import com.gettasksdone.dto.CheckItemDTO;
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.CheckItemRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.CheckItemService;
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
@RequestMapping("/check")
@SuppressWarnings("null")
@Tag(name = "Controlador de elementos de comprobación", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar los elementos de comprobación de la aplicación.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class CheckItemController {
    @Autowired
    private CheckItemRepository checkRepo;
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private CheckItemService checkService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getChecks")
    @Operation(
        summary = "Obtiene todos los elementos de comprobación.",
        description = "Se obtienen todos los elementos de comprobación registrados en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = CheckItemDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allChecks(){
		return new ResponseEntity<>(checkService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene los elementos de comprobación del usuario autenticado.",
        description = "Se obtienen los elementos de comprobación registrados en la aplicación por el usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = CheckItemDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> checkItemsFromUser(HttpServletRequest request) {
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(checkService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Busca un elemento de comprobación.",
        description = "Se busca un elemento de comprobación específico en la aplicación. Solamente el usuario que haya creado el elemento de comprobación o un administrador podrán verlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del elemento de comprobación a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Check Item not found."), mediaType = "string")}, description = "El elemento de comprobación no existe en la aplicación, o no se tiene permiso para consultarlo."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = CheckItemDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<CheckItem> check = checkRepo.findById(id);
        if(check.isEmpty()){
            return new ResponseEntity<>("Check Item not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = check.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(checkService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Check Item not found.", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/create")
    @Operation(
        summary = "Crea un elemento de comprobación.",
        description = "Se da de alta un contexto para una tarea en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.QUERY, required = true, name = "TaskID", description = "Identificador de la tarea que contendrá al elemento de comprobación", schema = @Schema(format = "TaskID={id}", example = "TaskID=1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del elemento de comprobación.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"contenido\": \"Check de prueba\", \"esta_marcado\": false}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Task not found."), mediaType = "string")}, description = "La tarea no existe en la aplicación, o no se tiene permiso para interactuar con ella."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Long.class), mediaType = "string")}, description = "El elemento de comprobación se ha creado correctamente. Se retorna el identificador del elemento de comprobación nuevo.")
    })
    public ResponseEntity<?> createCheck(@RequestBody CheckItem check, @RequestParam("TaskID") Long id, HttpServletRequest request){
        CheckItem cItem;
        List<CheckItem> items;
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Long ownerID = task.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                check.setUsuario(authedUser);
                check.setTarea(task.get());
                cItem = checkRepo.save(check);
                items = task.get().getCheckItems();
                items.add(cItem);
                task.get().setCheckItems(items);
                tareaRepo.save(task.get());
                return new ResponseEntity<>(cItem.getId(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica un elemento de comprobación.",
        description = "Se modifica un elemento de comprobación existente en la aplicación. Solo el usuario que ha creado el elemento de comprobación o un administrador pueden modificarlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del elemento de comprobación a modificar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del elemento de comprobación.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"contenido\": \"Check de prueba\", \"esta_marcado\": false}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Check Item updated."), mediaType = "string")}, description = "El elemento de comprobación se ha modificado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "Check Item not found."), mediaType = "string")}, description = "No se ha encontrado el elemento de comprobación, o no se tiene autorización para modificarlo.")
    })
    public ResponseEntity<?> updateCheck(@RequestBody CheckItem check, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<CheckItem> checkItem = checkRepo.findById(id);
        if(checkItem.isEmpty()){
            return new ResponseEntity<>("Check Item not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = checkItem.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            checkItem.get().setContenido(check.getContenido());
            checkItem.get().setEsta_marcado(check.isEsta_marcado());
            checkRepo.save(checkItem.get());
            return new ResponseEntity<>("Check Item updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Check Item not found.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina un elemento de comprobación.",
        description = "Se borra un elemento de comprobación de la aplicación. Solo el usuario que ha creado el elemento de comprobación o un administrador pueden borrarlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del elemento de comprobación a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Check Item deleted."), mediaType = "string")}, description = "El elemento de comprobación se ha eliminado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Check Item not found."), mediaType = "string")}, description = "No se ha encontrado el elemento de comprobación, o no se tiene autorización para eliminarlo.")
    })
    public ResponseEntity<String> deleteCheck(@PathVariable("id") Long id, HttpServletRequest request){
        if(checkRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Check item not found", HttpStatus.NOT_FOUND);
        }else{
            checkRepo.deleteById(id);
            return new ResponseEntity<>("Check Item deleted", HttpStatus.OK);
        }
    }
}
