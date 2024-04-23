package com.gettasksdone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para enviar como respuesta la información de un contexto.")
public class ContextoDTO {
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "Base de datos")
    String nombre;
}
