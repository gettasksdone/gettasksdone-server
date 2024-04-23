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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
@Schema(description = "Recurso para la gestión de tareas en la aplicación.")
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true, example = "1")
    private Long id;
    @ManyToOne
    @Schema(required = true, description = "Contexto asociado a la tarea.")
    private Contexto contexto;
    @Column(nullable = false)
    @Schema(required = true, example = "Tarea de prueba")
    private String titulo;
    @Column(nullable = false)
    @Schema(required = true, example = "Esta es una tarea de prueba.")
    private String descripcion;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(required = true, example = "2024-31-12 23:59:59")
    private LocalDateTime creacion;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(required = true, example = "2024-31-12 23:59:59")
    private LocalDateTime vencimiento;
    @Column(nullable = false)
    @Schema(required = true, description = "Estado actual de la tarea.")
    private String estado;
    @Column(nullable = false)
    @Schema(required = true, description = "Indica si la tarea es prioritaria (1) o no (0).")
    private int prioridad;
    @OneToOne
    @Schema(required = true, description = "Usuario propietario de la tarea.")
    private Usuario usuario;
    @ManyToOne
    @Schema(required = true, description = "Proyecto que tiene asignada esta tarea.")
    private Proyecto proyecto;
    @OneToMany
    @Schema(required = false, description = "Lista de elementos de comprobación asignados a la tarea.")
    private List<CheckItem> checkItems = new ArrayList<>();
    @OneToMany
    @Schema(required = false, description = "Lista de notas asignadas a la tarea.")
    private List<Nota> notas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "etiqueta_tarea",
        joinColumns=
            @JoinColumn(name="tarea_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_etiqueta_id", referencedColumnName="id")
    )
    @Schema(required = false, description = "Lista de etiquetas asignadas a la tarea.")
    private List<Etiqueta> etiquetas = new ArrayList<>();
}
