package com.gettasksdone.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para el envío de la información de una nota.")
public class NotaDTO {
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "Esto es una nota.")
    String contenido;
    @Schema(required = true, example = "2024-12-31 23:59:59")
    LocalDateTime creacion;
}
