package com.gettasksdone.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Contiene los parámetros necesarios para iniciar sesión.")
public class LoginRequest {
    @Schema(required = true, example = "userGTD")
    String username;
    @Schema(required = true, example = "contraseñaMuySegura1234")
    String password;
}
