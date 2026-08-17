package com.conquistadores.gestionclub.modules.poa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "bloques_cronograma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloqueCronograma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloque")
    private Long idBloque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cronograma", nullable = false)
    private Cronograma cronograma;

    @Column(name = "hora_inicio", nullable = false)
    private String horaInicio; // e.g. "14:00"

    @Column(name = "hora_fin", nullable = false)
    private String horaFin;    // e.g. "14:15"

    @Column(nullable = false)
    private String titulo;     // e.g. "Opening Ceremony"

    private String descripcion;

    private String tipo;       // e.g. "FIXED", "FLEXIBLE"
}
