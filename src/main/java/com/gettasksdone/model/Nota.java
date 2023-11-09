package com.gettasksdone.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String contenido;
    @Column(nullable = false)
    private LocalDateTime creacion;
    @ManyToMany
    @JoinTable(name = "nota_tarea",
        joinColumns=
            @JoinColumn(name="id_nota_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="tarea_id", referencedColumnName="id")
    )
    private List<Tarea> tareas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "nota_proyecto",
        joinColumns=
            @JoinColumn(name="id_nota_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="proyecto_id", referencedColumnName="id")
    )
    private List<Proyecto> proyectos = new ArrayList<>();
}