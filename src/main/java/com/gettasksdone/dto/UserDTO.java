package com.gettasksdone.dto;

import com.gettasksdone.model.Usuario.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para el envío de la información de acceso de un usuario")
public class UserDTO{
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "usuarioPrueba")
    String username;
    @Schema(required = true, example = "prueba@gettasksdone.com")
    private String email;
    @Schema(required = true, description = "Nivel de privilegios", example = "0")
    private Rol rol;
}
