package com.gettasksdone.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Recurso para la petición de registro en el sistema.")
public class RegisterRequest {
    @Schema(required = true, example = "usuarioDePrueba")
    String username;
    @Schema(required = true, example = "contraseñaMuySegura")
    String password;
    @Schema(required = true, example = "prueba@gettasksdone.com")
    String email;
}
