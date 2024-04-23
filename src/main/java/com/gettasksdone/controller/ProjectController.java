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
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.dto.ProyectoDTO;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.ProyectoService;
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
import com.gettasksdone.repository.EtiquetaRepository;

@RestController
@RequestMapping("/project")
@SuppressWarnings("null")
@Tag(name = "Controlador de proyectos", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar los proyectos de la aplicación.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class ProjectController {
    
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private ProyectoService proyectoService;

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene los proyectos del usuario autenticado.",
        description = "Se obtienen los proyectos registrados en la aplicación por el usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ProyectoDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> projectsFromUser(HttpServletRequest request) {
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(proyectoService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }
    

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getProjects")
    @Operation(
        summary = "Obtiene todos los proyectos.",
        description = "Se obtienen todos los proyectos registrados en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ProyectoDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allProjects(){
		return new ResponseEntity<>(proyectoService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    @Operation(
        summary = "Busca un proyecto.",
        description = "Se busca un proyecto específico en la aplicación. Solamente el usuario que haya creado el proyecto o un administrador podrán verlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del proyecto a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Project not found."), mediaType = "string")}, description = "El proyecto no existe en la aplicación, o no se tiene permiso para consultarlo."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = ProyectoDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = project.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(proyectoService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Project not found.", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/create")
    @Operation(
        summary = "Crea un proyecto.",
        description = "Se da de alta un proyecto en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del proyecto.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Proyecto de prueba\", \"inicio\": \"2024-12-31 23:59:59\", \"fin\": \"2024-12-31 23:59:59\", \"descripcion\": \"Este es un proyecto de prueba\", \"estado\": \"agendado\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Long.class), mediaType = "string")}, description = "El proyecto se ha creado correctamente. Se retorna el identificador del proyecto nuevo."),
        @ApiResponse(responseCode = "400", content = {@Content(examples = @ExampleObject(name = "Se intenta crear un proyecto de nombre inbox", description = "No es posible crear un proyecto con nombre inbox, ya que es un nombre de proyecto reservado para la bandeja de entrada.", value = "Inbox is a reservated project name."), mediaType = "text/plain")}, description = "Se ha intentado crear un proyecto inbox nuevo.")
    })
    public ResponseEntity<?> createProject(@RequestBody Proyecto project, HttpServletRequest request){
        Optional<Usuario> user = usuarioRepo.findById(MHelpers.getIdToken(request));
        if(project.getNombre().toLowerCase().equals("inbox")){
            return new ResponseEntity<>("Inbox is a reservated project name.", HttpStatus.BAD_REQUEST);
        }
        project.setUsuario(user.get());
        Proyecto proyecto = proyectoRepo.save(project);
        return new ResponseEntity<>(proyecto.getId(), HttpStatus.OK);
    }
    
    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica un proyecto.",
        description = "Se modifica un proyecto existente en la aplicación. Solo el usuario que ha creado el proyecto o un administrador pueden modificarlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del proyecto a modificar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada del proyecto.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"nombre\": \"Proyecto de prueba\", \"inicio\": \"2024-12-31 23:59:59\", \"fin\": \"2024-12-31 23:59:59\", \"descripcion\": \"Este es un proyecto de prueba\", \"estado\": \"agendado\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Project updated."), mediaType = "string")}, description = "El proyecto se ha modificado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "No se ha encontrado el proyecto, o no se tiene autorización para modificarlo.", value = "Project not found."),
                                                                                                      @ExampleObject(name = "Se intenta modificar el proyecto inbox", value = "Inbox project is protected from deletion or modification.", description = "No es posible modificar el proyecto inbox, ya que es un proyecto reservado para la bandeja de entrada.") } )},
                    description = "Ha ocurrido un error al procesar la solicitud.")
    })
    public ResponseEntity<?> updateProject(@PathVariable("id") Long id, @RequestBody Proyecto project, HttpServletRequest request){
        Optional<Proyecto> proyecto = proyectoRepo.findById(id);
        if(proyecto.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = proyecto.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            if(proyecto.get().getNombre().equals("inbox")){
                return new ResponseEntity<>("Inbox project is protected from deletion or modification", HttpStatus.BAD_REQUEST);
            }
            proyecto.get().setNombre(project.getNombre());
            proyecto.get().setDescripcion(project.getDescripcion());
            proyecto.get().setEstado(project.getEstado());
            proyecto.get().setInicio(project.getInicio());
            proyecto.get().setFin(project.getFin());
            proyectoRepo.save(proyecto.get());
            return new ResponseEntity<>("Project updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina un proyecto.",
        description = "Se borra un proyecto de la aplicación. Solo el usuario que ha creado el proyecto o un administrador pueden borrarlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del proyecto a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Project deleted."), mediaType = "string")}, description = "El proyecto se ha eliminado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "Inbox project is protected from deletion or modification"), mediaType = "text/plain")}, description = "No es posible eliminar el proyecto inbox, ya que es un proyecto reservado para la bandeja de entrada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Project not found."), mediaType = "string")}, description = "No se ha encontrado el proyecto, o no se tiene autorización para eliminarlo.")
    })
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = project.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                if(project.get().getNombre().equals("inbox")){
                    return new ResponseEntity<>("Inbox project is protected from deletion or modification", HttpStatus.BAD_REQUEST);
                }
                proyectoRepo.deleteById(id);
                return new ResponseEntity<>("Project deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PatchMapping("/addTag/{id}")
    @Operation(
        summary = "Añade una etiqueta a un proyecto.",
        description = "Se añade una etiqueta a un proyecto existente en la aplicación. Solo el usuario que ha creado el proyecto y etiqueta o un administrador puede interactuar con estos componentes.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del proyecto al que se le añadirá la etiqueta", schema = @Schema(format = "{id}", example = "1")),
            @Parameter(in = ParameterIn.QUERY, required = true, name = "TagID", description = "Identificador de la etiqueta a agregar", schema = @Schema(format = "TagID={id}", example = "TagID=1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Tag added to project."), mediaType = "string")}, description = "La etiqueta se ha añadido al proyecto correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "No se ha encontrado el proyecto, o no se tiene autorización para modificarlo.", value = "Project not found."),
                                                                                                      @ExampleObject(name = "La etiqueta ya está presente en el proyecto.", value = "Tag already on the project."),
                                                                                                      @ExampleObject(name = "No se ha encontrado la etiqueta, o no se tiene autorización para utilizarla.", value = "Tag not found.") } )},
                    description = "Ha ocurrido un error al procesar la solicitud.")
    })
    public ResponseEntity<?> addTagToProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found", HttpStatus.BAD_REQUEST);
            }else{
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                Long tagOwner = tag.get().getUsuario().getId();
                if(MHelpers.checkAccess(tagOwner, authedUser)){
                    Long ownerID = project.get().getUsuario().getId();
                    if(MHelpers.checkAccess(ownerID, authedUser)){
                        List<Etiqueta> tagList = project.get().getEtiquetas();
                        if(tagList.contains(tag.get())){
                            return new ResponseEntity<>("Tag already on the project.", HttpStatus.BAD_REQUEST);
                        }
                        tagList.add(tag.get());
                        project.get().setEtiquetas(tagList);
                        proyectoRepo.save(project.get());
                        return new ResponseEntity<>("Tag added to project.", HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    @Operation(
        summary = "Elimina una etiqueta de un proyecto.",
        description = "Se quita una etiqueta de un proyecto existente en la aplicación. Solo el usuario que ha creado el proyecto y etiqueta o un administrador puede interactuar con estos componentes.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador del proyecto al que se le eliminará la etiqueta", schema = @Schema(format = "{id}", example = "1")),
            @Parameter(in = ParameterIn.QUERY, required = true, name = "TagID", description = "Identificador de la etiqueta a remover", schema = @Schema(format = "TagID={id}", example = "TagID=1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Tag added to project."), mediaType = "string")}, description = "La etiqueta se ha eliminado del proyecto correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "No se ha encontrado el proyecto, o no se tiene autorización para modificarlo.", value = "Project not found."),
                                                                                                      @ExampleObject(name = "La etiqueta no está asignada en el proyecto.", value = "Tag not present in the project."),
                                                                                                      @ExampleObject(name = "No se ha encontrado la etiqueta, o no se tiene autorización para utilizarla.", value = "Tag not found.") } )},
                    description = "Ha ocurrido un error al procesar la solicitud.")
    })
    public ResponseEntity<?> delTagFromProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                Long tagOwner = tag.get().getUsuario().getId();
                if(MHelpers.checkAccess(tagOwner, authedUser)){
                    Long ownerID = project.get().getUsuario().getId();
                    if(MHelpers.checkAccess(ownerID, authedUser)){
                        List<Etiqueta> tagList = project.get().getEtiquetas();
                        if(!tagList.contains(tag.get())){
                            return new ResponseEntity<>("Tag not present in the project.", HttpStatus.BAD_REQUEST);
                        }
                        tagList.remove(tag.get());
                        project.get().setEtiquetas(tagList);
                        proyectoRepo.save(project.get());
                        return new ResponseEntity<>("Tag deleted from project.", HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }
}
