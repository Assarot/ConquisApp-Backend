package com.conquistadores.gestionclub.modules.miembros.model;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "historial_academico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historial_academico")
    private String idHistorialAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase", nullable = false)
    private Clase clase;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "especialidades_obtenidas", length = 1000)
    private String especialidadesObtenidas; // comma-separated values or list summary

    @Column(name = "requisitos_completados", length = 2000)
    private String requisitosCompletados; // summary of requirements completed

    @Column(name = "asistencia_resumen")
    private String asistenciaResumen; // e.g. "85%" or summary
}
