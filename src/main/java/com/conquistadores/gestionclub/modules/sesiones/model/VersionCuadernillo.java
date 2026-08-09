package com.conquistadores.gestionclub.modules.sesiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "versiones_cuadernillos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VersionCuadernillo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_version_cuadernillo")
    private String idVersionCuadernillo;

    @Column(name = "numero_version", nullable = false)
    private String numeroVersion;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;
}
