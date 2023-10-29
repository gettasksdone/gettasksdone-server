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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    @ManyToOne
    private Proyecto idProyecto;
    @ManyToOne
    private Contexto idContexto;
    @Column(nullable = false)
    private String descripcion;
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaVencimiento;
    @Column(nullable = false)
    private String estado;
    @Column(nullable = false)
    private int prioridad;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name ="idTarea")
    private List<CheckItem> checkItems = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "NotaTarea",
        joinColumns=
            @JoinColumn(name="idTarea", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idNota", referencedColumnName="id")
    )
    private List<Nota> notas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "EtiquetaTarea",
        joinColumns=
            @JoinColumn(name="idTarea", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idEtiqueta", referencedColumnName="id")
    )
    private List<Nota> etiquetas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "UsuarioTarea",
        joinColumns=
            @JoinColumn(name="idTarea", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idUsuario", referencedColumnName="id")
    )
    private List<Usuario> usuarios = new ArrayList<>();
}
