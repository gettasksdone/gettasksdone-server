package com.gettasksdone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para enviar como respuesta la información de una etiqueta")
public class EtiquetaDTO {
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "Urgente")
    String nombre;
}
