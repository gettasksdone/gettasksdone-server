package com.gettasksdone.dto;

import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Recurso para el envío de información de una tarea.")
public class TareaDTO {
    @Schema(required = true, example = "1")
    Long id;
    @Schema(required = true, example = "Tarea de prueba")
    String titulo;
    @Schema(required = false, example = "Esta es la descripción de una tarea.")
    String descripcion;
    @Schema(required = false, example = "2024-31-12 23:59:59")
    LocalDateTime creacion;
    @Schema(required = false, example = "2024-31-12 23:59:59")
    LocalDateTime vencimiento;
    @Schema(required = true, description = "Estado actual de la tarea.")
    String estado;
    @Schema(required = true, description = "Indica si la tarea es prioritaria o no.")
    int prioridad;
    @Schema(required = true, description = "Contexto asignado a la tarea")
    ContextoDTO contexto;
    @Schema(required = false, description = "Lista de elementos de comprobación de la tarea")
    List<CheckItemDTO> checkItems;
    @Schema(required = false, description = "Lista de notas de la tarea")
    List<NotaDTO> notas;
    @Schema(required = false, description = "Lista de etiquetas de la tarea")
    List<EtiquetaDTO> etiquetas;
    @Schema(required = true, description = "Identificador del proyecto asociado a la tarea.")
    Long proyectoId;
    @Schema(required = true, description = "Identificador del contexto asociado a la tarea.")
    Long contextoId;
}
