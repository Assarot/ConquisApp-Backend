package com.conquistadores.gestionclub.modules.especialidades.model;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "especialidad_clase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadClase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_especialidad_clase")
    private String idEspecialidadClase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase", nullable = false)
    private Clase clase;

    @Column(nullable = false)
    private String tipo; // "OBLIGATORIA" o "ADICIONAL"
}
