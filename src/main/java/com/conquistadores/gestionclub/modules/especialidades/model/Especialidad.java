package com.conquistadores.gestionclub.modules.especialidades.model;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Long idEspecialidad;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "requiere_examen", nullable = false)
    private Boolean requiereExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = true)
    @JsonIgnore
    private Club club;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria_especialidad")
    private CategoriaEspecialidad categoria;

    @Column(name = "nivel_destreza")
    private Integer nivelDestreza;

    @Column(name = "ano_introduccion")
    private Integer anoIntroduccion;

    @Column(name = "puntos_maestria")
    private Integer puntosMaestria;

    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;
}
