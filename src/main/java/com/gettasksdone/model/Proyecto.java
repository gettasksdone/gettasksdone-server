package com.gettasksdone.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Recurso para gestionar los proyectos de la aplicación.")
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true, example = "1")
    private Long id;
    @Column(nullable = false)
    @Schema(required = true, example = "Proyecto de pruebas")
    private String nombre;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(required = false, example = "2024-31-12 23:59:59")
    private LocalDateTime inicio;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(required = false, example = "2024-31-12 23:59:59")
    private LocalDateTime fin;
    @Schema(required = false, example = "Descripción del proyecto de pruebas.")
    private String descripcion;
    @Column(nullable = false)
    @Schema(required = true, description = "Estado actual del proyecto.")
    private String estado;
    @OneToOne
    @Schema(required = true, description = "Usuario propietario del proyecto.")
    private Usuario usuario;
    @OneToMany
    @Schema(required = false, description = "Lista de tareas asignadas al proyecto.")
    private List<Tarea> tareas = new ArrayList<>();
    @OneToMany
    @Schema(required = false, description = "Lista de notas asignadas al proyecto.")
    private List<Nota> notas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "etiqueta_proyecto",
        joinColumns=
            @JoinColumn(name="proyecto_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_etiqueta_id", referencedColumnName="id")
    )
    @Schema(required = false, description = "Lista de etiquetas asignadas al proyecto.")
    private List<Etiqueta> etiquetas = new ArrayList<>();
}
