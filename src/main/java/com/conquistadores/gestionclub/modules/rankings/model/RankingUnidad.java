package com.conquistadores.gestionclub.modules.rankings.model;

import com.conquistadores.gestionclub.modules.club.model.Unidad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "rankings_unidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingUnidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ranking")
    private Long idRanking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad", nullable = false)
    private Unidad unidad;

    @Column(nullable = false)
    private String periodo; // e.g. "2026-Q1", "2026-ANUAL"

    @Column(nullable = false)
    private Double puntaje;

    @Column(name = "reglamento_aplicado", length = 1000)
    private String reglamentoAplicado; // summary of applied scoring rule
}
