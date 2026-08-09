package com.conquistadores.gestionclub.modules.sesiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "requisitos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Requisito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_requisito")
    private String idRequisito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaRequisito categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_version_cuadernillo", nullable = false)
    private VersionCuadernillo versionCuadernillo;

    @Column(nullable = false, length = 1000)
    private String descripcion;
}
