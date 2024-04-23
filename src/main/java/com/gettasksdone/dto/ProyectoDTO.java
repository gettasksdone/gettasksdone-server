package com.gettasksdone.dto;

import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para el envío de información de un proyecto.")
public class ProyectoDTO {
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "Proyecto de pruebas")
    String nombre;
    @Schema(required = true, example = "2024-31-12 23:59:59")
    LocalDateTime inicio;
    @Schema(required = true, example = "2024-31-12 23:59:59")
    LocalDateTime fin;
    @Schema(required = true, description = "Descripción del proyecto")
    String descripcion;
    @Schema(required = true, description = "Estado actual del proyecto")
    String estado;
    @Schema(required = false, description = "Lista de tareas del proyecto")
    List<TareaDTO> tareas;
    @Schema(required = false, description = "Lista de notas del proyecto")
    List<NotaDTO> notas;
    @Schema(required = false, description = "Lista de etiquetas del proyecto")
    List<EtiquetaDTO> etiquetas;
}
