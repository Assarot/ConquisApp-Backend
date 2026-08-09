package com.conquistadores.gestionclub.modules.club.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "clubes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_club")
    private String idClub;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo; // "CONQUISTADORES" o "LIDERES"

    @Column(columnDefinition = "TEXT")
    private String configuracion; // Almacena JSON string para parámetros dinámicos
}
