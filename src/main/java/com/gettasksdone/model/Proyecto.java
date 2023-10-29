package com.gettasksdone.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private LocalDateTime fechaInicio;
    @Column(nullable = false)
    private LocalDateTime fechaFin;
    @Column(nullable = false)
    private String descripcion;
    @Column(nullable = false)
    private String estado;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name ="idProyecto")
    private List<Tarea> tareas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "NotaProyecto",
        joinColumns=
            @JoinColumn(name="idProyecto", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idNota", referencedColumnName="id")
    )
    private List<Nota> notas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "EtiquetaProyecto",
        joinColumns=
            @JoinColumn(name="idProyecto", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idEtiqueta", referencedColumnName="id")
    )
    private List<Etiqueta> etiquetas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "UsuarioProyecto",
        joinColumns=
            @JoinColumn(name="idProyecto", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idUsuario", referencedColumnName="id")
    )
    private List<Usuario> usuarios = new ArrayList<>();
}
