package com.gettasksdone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para enviar la información adicional de los usuarios.")
public class InfoUsuarioDTO {
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "Pedro")
    String nombre;
    @Schema(required = true, example = "123456789")
    long telefono;
    @Schema(required = true, example = "Recultador")
    String puesto;
    @Schema(required = true, example = "Recursos Humanos")
    String departamento;
    @Schema(required = true, description = "Identificador del usuario propietario de esta información adicional.")
    Long usuarioId;
}
