package com.gettasksdone.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
@Schema(description = "Recurso para la gestión de contextos en la aplicación.")
public class Contexto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true, example = "1")
    private Long id;
    @Column(nullable = false)
    @Schema(required = true, example = "Base de datos")
    private String nombre;
    @OneToOne
    @Schema(required = true, description = "Usuario creador de este contexto.")
    private Usuario usuario;
}
