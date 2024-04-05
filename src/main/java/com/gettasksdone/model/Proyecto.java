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
    @OneToOne
    private Usuario usuario;
    @OneToMany
    private List<Tarea> tareas = new ArrayList<>();
    @OneToMany
    private List<Nota> notas = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "etiqueta_proyecto",
        joinColumns=
            @JoinColumn(name="proyecto_id", referencedColumnName="id"),
        inverseJoinColumns=
            @JoinColumn(name="id_etiqueta_id", referencedColumnName="id")
    )
    private List<Etiqueta> etiquetas = new ArrayList<>();
}
