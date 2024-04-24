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
import com.gettasksdone.model.Tarea;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.dto.TareaDTO;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.TareaService;
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
import com.gettasksdone.repository.ContextoRepository;
import com.gettasksdone.repository.EtiquetaRepository;


@RestController
@RequestMapping("/task")
@SuppressWarnings("null")
@Tag(name = "Controlador de tareas", 
    description = "En este controlador se encuentran todas las operaciones relativas a gestionar las tareas de la aplicación.")
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, 
    name = "Authorization",
    in = SecuritySchemeIn.HEADER,
    scheme = "Authorization")
public class TasksController {
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private ContextoRepository contextoRepo;
    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private TareaService tareaService;

    @GetMapping("/authed")
    @Operation(
        summary = "Obtiene las tareas del usuario autenticado.",
        description = "Se obtienen las tareas registradas en la aplicación por el usuario autenticado actualmente.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = TareaDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> tasksFromUser(HttpServletRequest request) {
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        return new ResponseEntity<>(tareaService.findByUsuario(authedUser.get()), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getTasks")
    @Operation(
        summary = "Obtiene todos las tareas.",
        description = "Se obtienen todos las tareas registradas en la aplicación. REQUIERE PRIVILEGIOS DE ADMINISTRADOR.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer [token]"))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = TareaDTO.class)), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
	public ResponseEntity<?> allTasks(){
		return new ResponseEntity<>(tareaService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    @Operation(
        summary = "Busca una tarea.",
        description = "Se busca una tarea específica en la aplicación. Solamente el usuario que haya creado la tarea o un administrador podrán verlo.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la tarea a consultar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Task not found."), mediaType = "string")}, description = "La tarea no existe en la aplicación, o no se tiene permiso para consultarla."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = TareaDTO.class), mediaType = "application/json")}, description = "La llamada ha respondido correctamente.")
    })
    public ResponseEntity<?> findById(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = task.get().getUsuario().getId();
            Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
            if(MHelpers.checkAccess(ownerID, authedUser.get())){
                return new ResponseEntity<>(tareaService.findById(id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Task not found.", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PostMapping("/create")
    @Operation(
        summary = "Crea una tarea.",
        description = "Se da de alta una tarea en la aplicación.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.QUERY, required = false, name = "ProjectID", description = "Identificador del proyecto a donde se desea asignar la tarea", schema = @Schema(format = "ProjectID={id}", example = "ProjectID=3"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada de la tarea.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"contexto\": {\"id\": 1}, \"titulo\": \"Tarea de prueba\", \"descripcion\": \"Este es un proyecto de prueba\", \"creacion\": \"2024-12-31 23:59:59\", \"vencimiento\": \"2024-12-31 23:59:59\", \"estado\": \"agendado\", \"prioridad\": 0}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Long.class), mediaType = "string")}, description = "La tarea se ha creado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "El contexto especificado no se ha encontrado en el sistema, o no se tiene permisos para utilizarlo", value = "Context not found."),
                                                                                                      @ExampleObject(name = "El proyecto especificado no se ha encontrado en el sistema, o no se tiene permisos para utilizarlo", value = "Project not found.") } )},
                    description = "Ha ocurrido un error al procesar la solicitud.")
    })
    public ResponseEntity<?> createTask(@RequestBody Tarea task, @RequestParam(required = false, name = "ProjectID") Optional<Long> projectID, HttpServletRequest request){
        List<Tarea> projectTasks;
        if(!projectID.isEmpty()){
            Optional<Proyecto> project = proyectoRepo.findById(projectID.get());
            Tarea tarea;
            if(project.isEmpty()){
                return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
            }else{
                Long ownerID = project.get().getUsuario().getId();
                Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
                if(MHelpers.checkAccess(ownerID, authedUser.get())){
                    Optional<Usuario> user = usuarioRepo.findById(ownerID);
                    Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
                    if(context.isEmpty()){
                        return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
                    }else{
                        Long contextOwner = context.get().getUsuario().getId();
                        if(MHelpers.checkAccess(contextOwner, authedUser.get())){
                            task.setContexto(context.get());
                            task.setUsuario(user.get());
                            task.setProyecto(project.get());
                            tarea = tareaRepo.save(task);
                            projectTasks = project.get().getTareas();
                            projectTasks.add(tarea);
                            project.get().setTareas(projectTasks);
                            proyectoRepo.save(project.get());
                            return new ResponseEntity<>(tarea.getId(), HttpStatus.OK);
                        }else{
                            return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
                        }
                    }
                }else{
                    return new ResponseEntity<>("Project not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }else{
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            List<Proyecto> projects = proyectoRepo.findByUsuario(authedUser);
            Proyecto inbox = null;
            for(int i = 0; i < projects.size(); i++){
                if(projects.get(i).getNombre().equals("inbox")){
                    inbox = projects.get(i);
                    break;
                }
            }
            Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
            if(context.isEmpty()){
                return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
            }else{
                Long contextOwner = context.get().getUsuario().getId();
                if(MHelpers.checkAccess(contextOwner, authedUser)){
                    task.setContexto(context.get());
                    task.setUsuario(authedUser);
                    task.setProyecto(inbox);
                    Tarea tarea = tareaRepo.save(task);
                    projectTasks = inbox.getTareas();
                    projectTasks.add(tarea);
                    inbox.setTareas(projectTasks);
                    proyectoRepo.save(inbox);
                    return new ResponseEntity<>(tarea.getId(), HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    @PatchMapping("/update/{id}")
    @Operation(
        summary = "Modifica una tarea.",
        description = "Se modifica una tarea existente en la aplicación. Solo el usuario que ha creado la tarea o un administrador pueden modificarla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la tarea a modificar", schema = @Schema(format = "{id}", example = "1")),
            @Parameter(in = ParameterIn.QUERY, required = false, name = "ProjectID", description = "Identificador del proyecto a donde se desea mover la tarea", schema = @Schema(format = "ProjectID={id}", example = "ProjectID=3"))
        }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Parámetros de entrada de la tarea.", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"contexto\": {\"id\": 1}, \"titulo\": \"Tarea de prueba\", \"descripcion\": \"Este es un proyecto de prueba\", \"creacion\": \"2024-12-31 23:59:59\", \"vencimiento\": \"2024-12-31 23:59:59\", \"estado\": \"agendado\", \"prioridad\": 0}")))
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Task updated."), mediaType = "string")}, description = "La tarea se ha modificado correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "No se ha encontrado la tarea, o no se tiene autorización para modificarla.", value = "Task not found."),
                                                                                                      @ExampleObject(name = "No se ha encontrado el contexto, o no se tiene autorización para utilizarlo.", value = "Context not found."),
                                                                                                      @ExampleObject(name = "No se ha encontrado el proyecto, o no se tiene autorización para utilizarlo.", value = "Project not found.") } )},
                    description = "Ha ocurrido un error al procesar la solicitud.")
    })
    public ResponseEntity<?> updateTask(@RequestBody Tarea task, @PathVariable("id") Long id, @RequestParam(required = false, name = "ProjectID") Optional<Long> projectID, HttpServletRequest request){
        Optional<Tarea> tarea = tareaRepo.findById(id);
        Optional<Contexto> context = contextoRepo.findById(task.getContexto().getId());
        if(tarea.isEmpty()){
            return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
        }
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found", HttpStatus.BAD_REQUEST);
        }
        Optional<Usuario> authedUser = usuarioRepo.findById(MHelpers.getIdToken(request));
        Long ownerID = tarea.get().getUsuario().getId(), contextOwner = context.get().getUsuario().getId();
        if(MHelpers.checkAccess(ownerID, authedUser.get())){
            if(MHelpers.checkAccess(contextOwner, authedUser.get())){
                tarea.get().setTitulo(task.getTitulo());
                tarea.get().setDescripcion(task.getDescripcion());
                tarea.get().setVencimiento(task.getVencimiento());
                tarea.get().setEstado(task.getEstado());
                tarea.get().setPrioridad(task.getPrioridad());
                tarea.get().setContexto(context.get());
                if(!projectID.isEmpty()){ //Si no viene vacio es que hay que cambiar de proyecto
                    Optional<Proyecto> project = proyectoRepo.findById(projectID.get());
                    if(project.isEmpty()){
                        return new ResponseEntity<>("Project not found", HttpStatus.BAD_REQUEST);
                    }
                    Long projectOwner = project.get().getUsuario().getId();
                    if(MHelpers.checkAccess(projectOwner,authedUser.get())){
                        Proyecto oldProject = tarea.get().getProyecto();
                        List<Tarea> newTaskList = oldProject.getTareas();
                        for(int i = 0; i < newTaskList.size(); i++){ //Se busca la tarea en la tarea anterior y se elimina
                            if(newTaskList.get(i).getId() == tarea.get().getId()){
                                newTaskList.remove(i);
                                break;
                            }
                        }
                        oldProject.setTareas(newTaskList);
                        proyectoRepo.save(oldProject); //Se actualiza la tarea anterior
                        tarea.get().setProyecto(project.get());
                        tareaRepo.save(tarea.get());
                        newTaskList = project.get().getTareas();
                        newTaskList.add(tarea.get());
                        project.get().setTareas(newTaskList);
                        proyectoRepo.save(project.get());
                    }else{
                        return new ResponseEntity<>("Project not found", HttpStatus.BAD_REQUEST);
                    }
                }else{
                    tareaRepo.save(tarea.get());
                }
                return new ResponseEntity<>("Task updated.", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Context not found", HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
        summary = "Elimina una tarea.",
        description = "Se borra una tarea de la aplicación. Solo el usuario que ha creado la tarea o un administrador pueden borrarla.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la tarea a eliminar", schema = @Schema(format = "{id}", example = "1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Task deleted."), mediaType = "string")}, description = "La tarea se ha eliminado correctamente."),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(example = "Task not found."), mediaType = "string")}, description = "No se ha encontrado la tarea, o no se tiene autorización para eliminarla.")
    })
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }else{
            Long ownerID = task.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                tareaRepo.deleteById(id);
                return new ResponseEntity<>("Task deleted", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    @PatchMapping("/addTag/{id}")
    @Operation(
        summary = "Añade una etiqueta a una tarea.",
        description = "Se añade una etiqueta a una tarea existente en la aplicación. Solo el usuario que ha creado la tarea y etiqueta o un administrador puede interactuar con estos componentes.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la tarea al que se le añadirá la etiqueta", schema = @Schema(format = "{id}", example = "1")),
            @Parameter(in = ParameterIn.QUERY, required = true, name = "TagID", description = "Identificador de la etiqueta a agregar", schema = @Schema(format = "TagID={id}", example = "TagID=1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Tag added to task."), mediaType = "string")}, description = "La etiqueta se ha añadido a la tarea correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "No se ha encontrado la tarea, o no se tiene autorización para modificarla.", value = "Task not found."),
                                                                                                      @ExampleObject(name = "La etiqueta ya está presente en la tarea.", value = "Tag already on this task."),
                                                                                                      @ExampleObject(name = "No se ha encontrado la etiqueta, o no se tiene autorización para utilizarla.", value = "Tag not found.") } )},
                    description = "Ha ocurrido un error al procesar la solicitud.")
    })
    public ResponseEntity<?> addTagToTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                Long tagOwner = tag.get().getUsuario().getId();
                if(MHelpers.checkAccess(tagOwner, authedUser)){
                    Long ownerID = task.get().getUsuario().getId();
                    if(MHelpers.checkAccess(ownerID, authedUser)){
                        List<Etiqueta> tagList = task.get().getEtiquetas();
                        if(tagList.contains(tag.get())){
                            return new ResponseEntity<>("Tag already on this task.", HttpStatus.BAD_REQUEST);
                        }
                        tagList.add(tag.get());
                        task.get().setEtiquetas(tagList);
                        tareaRepo.save(task.get());
                        return new ResponseEntity<>("Tag added to task.", HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
                }
                
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    @Operation(
        summary = "Elimina una etiqueta de una tarea.",
        description = "Se quita una etiqueta de una tarea existente en la aplicación. Solo el usuario que ha creado la tarea y etiqueta o un administrador puede interactuar con estos componentes.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Token de autenticación", schema = @Schema(format = "Bearer {token}")),
            @Parameter(in = ParameterIn.PATH, required = true, name = "id", description = "Identificador de la tarea al que se le eliminará la etiqueta", schema = @Schema(format = "{id}", example = "1")),
            @Parameter(in = ParameterIn.QUERY, required = true, name = "TagID", description = "Identificador de la etiqueta a remover", schema = @Schema(format = "TagID={id}", example = "TagID=1"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "CONN_REFUSED", content = {@Content(schema = @Schema())} , description = "El servidor ha rechazado la conexión porque no se tiene autorización para acceder a esta llamada."),
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(example = "Tag deleted from task."), mediaType = "string")}, description = "La etiqueta se ha eliminado de la tarea correctamente."),
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "No se ha encontrado la tarea, o no se tiene autorización para modificarla.", value = "Task not found."),
                                                                                                      @ExampleObject(name = "La etiqueta no está asignada en la tarea.", value = "Tag not present on this task."),
                                                                                                      @ExampleObject(name = "No se ha encontrado la etiqueta, o no se tiene autorización para utilizarla.", value = "Tag not found.") } )},
                    description = "Ha ocurrido un error al procesar la solicitud.")
    })
    public ResponseEntity<?> delTagFromTask(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id, HttpServletRequest request){
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
            }else{
                Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
                Long tagOwner = tag.get().getUsuario().getId();
                if(MHelpers.checkAccess(tagOwner, authedUser)){
                    Long ownerID = task.get().getUsuario().getId();
                    if(MHelpers.checkAccess(ownerID, authedUser)){
                        List<Etiqueta> tagList = task.get().getEtiquetas();
                        if(!tagList.contains(tag.get())){
                            return new ResponseEntity<>("Tag not present on this task.", HttpStatus.BAD_REQUEST);
                        }
                        tagList.remove(tag.get());
                        task.get().setEtiquetas(tagList);
                        tareaRepo.save(task.get());
                        return new ResponseEntity<>("Tag deleted from task.", HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
                    }
                }else{
                    return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }
}
