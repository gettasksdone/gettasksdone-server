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
@Schema(description = "Recurso para gestionar la información adicional del usuario.")
public class InfoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true, example = "1")
    private Long id;
    @OneToOne
    @Schema(required = true, description = "Usuario propietario de esta información adicional.")
    private Usuario usuario;
    @Column(nullable = false)
    @Schema(required = true, example = "Pedro")
    private String nombre;
    @Column(nullable = true)
    @Schema(required = false, example = "123456789")
    private long telefono;
    @Column(nullable = false)
    @Schema(required = true, example = "Reclutador")
    private String puesto;
    @Column(nullable = false)
    @Schema(required = true, example = "Recursos Humanos")
    private String departamento;
}
