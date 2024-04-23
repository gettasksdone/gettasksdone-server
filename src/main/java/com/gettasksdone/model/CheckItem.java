package com.gettasksdone.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
@Schema(description = "Recurso para los elementos de comprobación.")
public class CheckItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true, example = "1")
    private Long id;
    @Column(nullable = false)
    @Schema(required = true, example = "Marcar esta casilla como completada")
    private String contenido;
    @Column(nullable = false)
    @Schema(required = true, description = "Determina si este paso de tarea ha sido completado.", example = "true")
    private boolean esta_marcado;
    @OneToOne
    @Schema(required = true, description = "Usuario que es propietario de esta casilla de comprobación.")
    private Usuario usuario;
    @ManyToOne
    @Schema(required = true, description = "Tarea asignada a esta casilla de comprobación.")
    private Tarea tarea;
}
