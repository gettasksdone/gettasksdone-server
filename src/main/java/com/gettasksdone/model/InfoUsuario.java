package com.gettasksdone.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class InfoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario idUsuario;
    @ManyToOne(cascade = CascadeType.ALL)
    private Usuario superior;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private int telefono;
    @Column(nullable = false)
    private String puesto;
    @Column(nullable = false)
    private String departamento;
}
