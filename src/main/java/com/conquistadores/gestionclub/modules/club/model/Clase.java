package com.conquistadores.gestionclub.modules.club.model;

import com.conquistadores.gestionclub.modules.sesiones.model.VersionCuadernillo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "clases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clase")
    private Long idClase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = true)
    @JsonIgnore
    private Club club;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 20)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_version_cuadernillo")
    private VersionCuadernillo versionCuadernillo;
}
