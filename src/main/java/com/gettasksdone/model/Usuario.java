package com.gettasksdone.model;

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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "superior")
    private List<InfoUsuario> infoUsuarios = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "UsuarioTarea",
        joinColumns=
            @JoinColumn(name="idUsuario", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idTarea", referencedColumnName="id")
    )
    private List<Tarea> tareas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "UsuarioProyecto",
        joinColumns=
            @JoinColumn(name="idUsuario", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="idProyecto", referencedColumnName="id")
    )
    private List<Proyecto> proyectos = new ArrayList<>();
}
