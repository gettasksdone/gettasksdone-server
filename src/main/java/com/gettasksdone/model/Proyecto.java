package com.gettasksdone.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
import lombok.Data;

@Entity
@Data
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inicio;
    @Column(nullable = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fin;
    @Column(nullable = false)
    private String descripcion;
    @Column(nullable = false)
    private String estado;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Tarea> tareas = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "nota_proyecto",
        joinColumns=
            @JoinColumn(name="proyecto_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_nota_id", referencedColumnName="id")
    )
    private List<Nota> notas = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "etiqueta_proyecto",
        joinColumns=
            @JoinColumn(name="proyecto_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_etiqueta_id", referencedColumnName="id")
    )
    private List<Etiqueta> etiquetas = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "UsuarioProyecto",
        joinColumns=
            @JoinColumn(name="proyecto_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_usuario_id", referencedColumnName="id")
    )
    private List<Usuario> usuarios = new ArrayList<>();
}
