package com.conquistadores.gestionclub.modules.club.model;

import com.conquistadores.gestionclub.modules.sesiones.model.VersionCuadernillo;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_clase")
    private String idClase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = false)
    private Club club;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_version_cuadernillo")
    private VersionCuadernillo versionCuadernillo;
}
