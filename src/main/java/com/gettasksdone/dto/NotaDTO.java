package com.gettasksdone.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotaDTO {
    String contenido;
    LocalDateTime creacion;
}
