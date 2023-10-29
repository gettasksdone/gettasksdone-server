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
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    @Column(nullable = false)
    private String contenido;
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    @ManyToMany
    @JoinTable(name = "NotaTarea",
        joinColumns=
            @JoinColumn(name="idNota", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idTarea", referencedColumnName="id")
    )
    private List<Tarea> tareas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "NotaProyecto",
        joinColumns=
            @JoinColumn(name="idNota", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idProyecto", referencedColumnName="id")
    )
    private List<Proyecto> proyectos = new ArrayList<>();
}
