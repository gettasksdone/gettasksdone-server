package com.gettasksdone.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import lombok.Data;

@Entity
@Data
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Contexto contexto;
    @Column(nullable = false)
    private String descripcion;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creacion;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime vencimiento;
    @Column(nullable = false)
    private String estado;
    @Column(nullable = false)
    private int prioridad;
    @OneToMany
    private List<CheckItem> checkItems = new ArrayList<>();
    @OneToMany
    private List<Nota> notas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "etiqueta_tarea",
        joinColumns=
            @JoinColumn(name="tarea_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_etiqueta_id", referencedColumnName="id")
    )
    private List<Nota> etiquetas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "usuario_tarea",
        joinColumns=
            @JoinColumn(name="tarea_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_usuario_id", referencedColumnName="id")
    )
    private List<Usuario> usuarios = new ArrayList<>();
}
