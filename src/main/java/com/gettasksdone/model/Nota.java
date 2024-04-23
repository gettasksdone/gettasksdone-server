package com.gettasksdone.model;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "Recurso para la gestión de notas dentro de la aplicación.")
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true, example = "1")
    private Long id;
    @Column(nullable = false)
    @Schema(required = true, example = "Esto es una nota.")
    private String contenido;
    @Column(nullable = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(required = true, example = "2024-12-31 23:59:59")
    private LocalDateTime creacion;
    @OneToOne
    @Schema(required = true, description = "Usuario propietario de la nota.")
    private Usuario usuario;
    @ManyToOne(optional = true)
    @Schema(required = false, description = "Proyecto asignado a la nota.")
    private Proyecto proyecto;
    @ManyToOne(optional = true)
    @Schema(required = false, description = "Tarea asignada a la nota.")
    private Tarea tarea;
}
