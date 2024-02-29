package com.gettasksdone.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ProyectoDTO {
    String nombre;
    LocalDateTime inicio;
    LocalDateTime fin;
    String descripcion;
    String estado;
    List<TareaDTO> tareas;
    List<NotaDTO> notas;
    List<EtiquetaDTO> etiquetas;
}
