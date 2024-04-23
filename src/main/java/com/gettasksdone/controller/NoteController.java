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

import com.gettasksdone.dto.NotaDTO;
import com.gettasksdone.model.Nota;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.NotaRepository;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.NotaService;
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
@RequestMapping("/note")
@SuppressWarnings("null")
@Tag(name = "Controlador de notas", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar las notas de la aplicación.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class NoteController {
    @Autowired
    private NotaRepository notaRepo;
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private NotaService notaService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getNotes")
    @Operation(
        summary = "Obtiene todas las notas.",
        description = "Se obtienen todas las notas registradas en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = NotaDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allNotes(){
		return new ResponseEntity<>(notaService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene las notas del usuario autenticado.",
        description = "Se obtienen las notas registradas en la aplicación por el usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = NotaDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> notesFromUser(HttpServletRequest request){
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(notaService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Busca una nota.",
        description = "Se busca una nota específica en la aplicación. Solamente el usuario que haya creado la nota o un administrador podrán verla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la nota a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Note not found."), mediaType = "string")}, description = "La nota no existe en la aplicación, o no se tiene permiso para consultarla."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = NotaDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Nota> note = notaRepo.findById(id);
        if(note.isEmpty()){
            return new ResponseEntity<>("Note not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = note.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                return new ResponseEntity<>(notaService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Note not found.", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/create")
    @Operation(
        summary = "Crea una nota.",
        description = "Se da de alta una nota en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.QUERY, required = true, name = "Target", description = "Entidad a la que se le asignará la nota. Puede ser Project o Task", schema = @Schema(format = "Target={id}", example = "Target=Task")),
            @Parameter(in = ParameterIn.QUERY, required = true, name = "ID", description = "Identificador de la tarea/proyecto donde se asignará la nota.", schema = @Schema(format = "ID={id}", example = "ID=1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada de la nota.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"contenido\": \"Nota de prueba\", \"creacion\": \"2024-12-31 23:59:59\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Long.class), mediaType = "string")}, description = "La nota se ha creado correctamente. Se retorna el identificador de la nota nueva."),
        @ApiResponse(responseCode = "400", content = {@Content(examples = { @ExampleObject(name = "No se ha encontrado el proyecto o no se tiene permisos.", value = "Project not found."),
                                                                            @ExampleObject(name = "No se ha encontrado la tarea o no se tiene permisos.", value = "Task not found."),
                                                                            @ExampleObject(name = "No se ha asignado la nota a un valor correcto.", value = "Must assign the note to a task or a project.")
                                                                        }, mediaType = "text/plain" )}, description = "Ha ocurrido un error al realizar la llamada.")
    })
    public ResponseEntity<?> createNote(@RequestParam("Target") String target, @RequestParam("ID") Long id, @RequestBody Nota note, HttpServletRequest request){
        Nota nota;
        List<Nota> notas;
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        Long ownerID;
        if(target.equals("Project")){
            Optional<Proyecto> project = proyectoRepo.findById(id);
            if(project.isEmpty()){
                return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
            }else{
                ownerID = project.get().getUsuario().getId();
                if(!MHelpers.checkAccess(ownerID, authedUser)){
                    return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
                }
                note.setUsuario(authedUser);
                note.setProyecto(project.get());
                note.setTarea(null);
                nota = notaRepo.save(note);
                notas = project.get().getNotas();
                notas.add(nota);
                project.get().setNotas(notas);
                proyectoRepo.save(project.get());
            }
        }else if(target.equals("Task")){
            Optional<Tarea> task = tareaRepo.findById(id);
            if(task.isEmpty()){
                return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
            }else{
                ownerID = task.get().getUsuario().getId();
                if(!MHelpers.checkAccess(ownerID, authedUser)){
                    return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
                }
                note.setUsuario(authedUser);
                note.setProyecto(null);
                note.setTarea(task.get());
                nota = notaRepo.save(note);
                notas = task.get().getNotas();
                notas.add(nota);
                task.get().setNotas(notas);
                tareaRepo.save(task.get());
            }
        }else{
            return new ResponseEntity<>("Must assign the note to a task or a project.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(nota.getId(), HttpStatus.OK);
    }

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica una nota.",
        description = "Se modifica una nota existente en la aplicación. Solo el usuario que ha creado la nota o un administrador pueden modificarla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la nota a modificar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada de la nota.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"contenido\": \"Nota de prueba\", \"creacion\": \"2024-12-31 23:59:59\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Note updated."), mediaType = "string")}, description = "La nota se ha modificado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(example = "Note not found."), mediaType = "string")}, description = "No se ha encontrado la nota, o no se tiene autorización para modificarla.")
    })
    public ResponseEntity<?> updateNote(@PathVariable("id") Long id, @RequestBody Nota note, HttpServletRequest request){
        Optional<Nota> nota = notaRepo.findById(id);
        if(nota.isEmpty()){
            return new ResponseEntity<>("Note not found.", HttpStatus.BAD_REQUEST);
        }
        Long ownerID = nota.get().getUsuario().getId();
        Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
        if(MHelpers.checkAccess(ownerID, authedUser)){
            nota.get().setContenido(note.getContenido());
            notaRepo.save(nota.get());
            return new ResponseEntity<>("Note updated.", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Note not found.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina una nota.",
        description = "Se borra una nota de la aplicación. Solo el usuario que ha creado la nota o un administrador pueden borrarla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la nota a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Note deleted."), mediaType = "string")}, description = "La nota se ha eliminado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Note not found."), mediaType = "string")}, description = "No se ha encontrado la nota, o no se tiene autorización para eliminarla.")
    })
    public ResponseEntity<String> deleteNote(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Nota> nota = notaRepo.findById(id);
        if(nota.isEmpty()){
            return new ResponseEntity<>("Note not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = nota.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                notaRepo.deleteById(id);
                return new ResponseEntity<>("Note deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Note not found", HttpStatus.NOT_FOUND);
            }
        }
    }
}
