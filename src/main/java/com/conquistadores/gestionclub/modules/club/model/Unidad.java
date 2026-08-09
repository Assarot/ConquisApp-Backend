package com.conquistadores.gestionclub.modules.club.model;

import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "unidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Unidad {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_unidad")
    private String idUnidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = false)
    private Club club;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consejero")
    private Usuario consejero;

    private String color;
    private String icono;
    private String descripcion;
}
