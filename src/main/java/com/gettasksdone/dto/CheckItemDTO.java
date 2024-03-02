package com.gettasksdone.dto;

import lombok.Data;

@Data
public class CheckItemDTO {
    Long id;
    String contenido;
    boolean esta_marcado;
}
