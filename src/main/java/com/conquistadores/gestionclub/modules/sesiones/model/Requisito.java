package com.conquistadores.gestionclub.modules.sesiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;

@Entity
@Table(name = "requisitos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Requisito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requisito")
    private Long idRequisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = true)
    private CategoriaRequisito categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_version_cuadernillo", nullable = true)
    private VersionCuadernillo versionCuadernillo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase", nullable = true)
    private Clase clase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialidad", nullable = true)
    private Especialidad especialidad;

    @Column(name = "es_avanzado")
    private Boolean esAvanzado;

    @Column(nullable = false, length = 1000)
    private String descripcion;
}
