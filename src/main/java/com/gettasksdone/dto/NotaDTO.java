package com.gettasksdone.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NotaDTO {
    Long id;
    String contenido;
    LocalDateTime creacion;
}
