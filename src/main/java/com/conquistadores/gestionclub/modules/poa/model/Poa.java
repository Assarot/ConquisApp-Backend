package com.conquistadores.gestionclub.modules.poa.model;

import com.conquistadores.gestionclub.modules.club.model.Club;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "poas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Poa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_poa")
    private String idPoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = false)
    private Club club;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private String estado; // e.g. "BORRADOR", "PUBLICADO"
}
