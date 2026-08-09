package com.conquistadores.gestionclub.modules.especialidades.model;

import com.conquistadores.gestionclub.modules.club.model.Club;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "especialidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Especialidad {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_especialidad")
    private String idEspecialidad;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "requiere_examen", nullable = false)
    private Boolean requiereExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = false)
    private Club club;

    private String categoria;

    @Column(name = "puntos_maestria")
    private Integer puntosMaestria;

    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;
}
