package com.gettasksdone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para enviar los elementos de comprobación.")
public class CheckItemDTO {
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "Este es un elemento de comprobación.")
    String contenido;
    @Schema(required = true, example = "false")
    boolean esta_marcado;
}
