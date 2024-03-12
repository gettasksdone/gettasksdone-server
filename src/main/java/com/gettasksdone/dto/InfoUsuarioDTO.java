package com.gettasksdone.dto;

import lombok.Data;

@Data
public class InfoUsuarioDTO {
    Long id;
    String nombre;
    long telefono;
    String puesto;
    String departamento;
}
