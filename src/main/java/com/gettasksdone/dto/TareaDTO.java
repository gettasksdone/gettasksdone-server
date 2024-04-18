package com.gettasksdone.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class TareaDTO {
    Long id;
    String titulo;
    String descripcion;
    LocalDateTime creacion;
    LocalDateTime vencimiento;
    String estado;
    int prioridad;
    ContextoDTO contexto;
    List<CheckItemDTO> checkItems;
    List<NotaDTO> notas;
    List<EtiquetaDTO> etiquetas;
}
