package com.gettasksdone.dto;

import lombok.Data;

@Data
public class InfoUsuarioDTO {
    Long id;
    String nombre;
    int telefono;
    String puesto;
    String departamento;
}
