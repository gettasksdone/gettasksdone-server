package com.gettasksdone.auth;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Controlador de autorización", 
    description = "Este controlador se encarga de manejar todas las operaciones de inicio de sesión y registro.")
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/login", consumes = "application/json")
    @Operation(
        summary = "Inicia la sesión de un usuario.",
        description = "Se intenta iniciar la sesión de un usuario en el sistema."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Credenciales de acceso del usuario.",
        required = true, 
        content = @Content(
            mediaType = "application/json", 
            examples = @ExampleObject(
                value = "{\"username\": \"usuarioDePrueba\",\"password\": \"userPassword\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "403", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "Las credenciales de acceso no son válidas", value = "Invalid credentials."),
                                                                                                      @ExampleObject(name = "La cuenta con la que se ha intentado iniciar sesión ha sido deshabilitada.", value = "This account has been disabled. Contact your administrator." ),
                                                                                                      @ExampleObject(name = "La cuenta con la que se ha intentado iniciar sesión ha sido bloqueada.", value = "This account has been locked. Contact your administrator." ) }  ) } , description = "Ha ocurrido un error en la solicitud."),
        @ApiResponse(responseCode = "200", content = {@Content(examples = @ExampleObject(name = "Se proporciona el token de autenticación", value = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjMiLCJyb2wiOiJVU1VBUklPIiwic3ViIjoiY2Rpc2lkb3IiLCJpYXQiOjE3MDkyMTk1MTMsImV4cCI6MTcwOTIyMDk1M30.-k-k_kTqTJ_B1zzNwRHO7vtEY6fD17M4CkN2cVLh-r4"), mediaType = "string")}, description = "Se ha iniciado la sesión del usuario. Se retorna el token JWT generado por el servidor.")
    })
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping(value = "/sudoRegister", consumes = "application/json")
    @Operation(
        summary = "Se da de alta un usuario administrador.",
        description = "Se intenta registrar a un usuario administrador en el sistema."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Credenciales de registro del usuario.",
        required = true, 
        content = @Content(
            mediaType = "application/json", 
            examples = @ExampleObject(
                value = "{\"username\": \"usuarioDePrueba\",\"password\": \"userPassword\", \"email\": \"sudoEmail@gettasksdone.com\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "El nombre de usuario ya está en uso dentro de la aplicación.", value = "The username already exists."),
                                                                                                      @ExampleObject(name = "La dirección de correo electrónico ya está en uso dentro de la aplicación.", value = "The email already exists." ) }  ) } , description = "Ha ocurrido un error en la solicitud."),
        @ApiResponse(responseCode = "401", content = {@Content(examples = @ExampleObject(name = "Ya existe un usuario administrador en el sistema.", value = "Unauthorized"), mediaType = "string")}, description = "Ya se ha dado de alta un usuario administrador en el sistema, por lo que no pueden darse de alta más administradores."),
        @ApiResponse(responseCode = "200", content = {@Content(examples = @ExampleObject(name = "Se proporciona el token de autenticación", value = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjMiLCJyb2wiOiJVU1VBUklPIiwic3ViIjoiY2Rpc2lkb3IiLCJpYXQiOjE3MDkyMTk1MTMsImV4cCI6MTcwOTIyMDk1M30.-k-k_kTqTJ_B1zzNwRHO7vtEY6fD17M4CkN2cVLh-r4"), mediaType = "string")}, description = "Se ha dado de alta al usuario. Se retorna el token JWT generado por el servidor.")
    })
    public ResponseEntity<String> sudoRegister(@RequestBody RegisterRequest request) { //Funciona SOLO si no hay otro administrador en el sistema
        return authService.sudoRegister(request);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    @Operation(
        summary = "Se da de alta un usuario.",
        description = "Se intenta registrar a un usuario en el sistema."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Credenciales de registro del usuario.",
        required = true, 
        content = @Content(
            mediaType = "application/json", 
            examples = @ExampleObject(
                value = "{\"username\": \"usuarioDePrueba\",\"password\": \"userPassword\", \"email\": \"userEmail@gettasksdone.com\"}")))
    @ApiResponses({
        @ApiResponse(responseCode = "400", content = {@Content(mediaType = "text/plain", examples = { @ExampleObject(name = "El nombre de usuario ya está en uso dentro de la aplicación.", value = "The username already exists."),
                                                                                                      @ExampleObject(name = "La dirección de correo electrónico ya está en uso dentro de la aplicación.", value = "The email already exists." ) }  ) } , description = "Ha ocurrido un error en la solicitud."),
        @ApiResponse(responseCode = "200", content = {@Content(examples = @ExampleObject(name = "Se proporciona el token de autenticación", value = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjMiLCJyb2wiOiJVU1VBUklPIiwic3ViIjoiY2Rpc2lkb3IiLCJpYXQiOjE3MDkyMTk1MTMsImV4cCI6MTcwOTIyMDk1M30.-k-k_kTqTJ_B1zzNwRHO7vtEY6fD17M4CkN2cVLh-r4"), mediaType = "string")}, description = "Se ha dado de alta al usuario. Se retorna el token JWT generado por el servidor.")
    })
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping(value = "/oauth")
    @Hidden
    public ResponseEntity<String> oauthManager(HttpServletRequest request,  HttpServletResponse response) {
        return authService.manageOAuth(request);
    }
}
