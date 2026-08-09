package com.conquistadores.gestionclub.modules.sesiones.model;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "sesiones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_sesion")
    private String idSesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase", nullable = false)
    private Clase clase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instructor", nullable = false)
    private Usuario instructor;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer duracion; // en minutos

    @Column(length = 2000)
    private String actividades;

    @Column(length = 1000)
    private String materiales;

    @Column(length = 1000)
    private String evaluacion;

    @ManyToMany
    @JoinTable(
        name = "sesion_requisito",
        joinColumns = @JoinColumn(name = "id_sesion"),
        inverseJoinColumns = @JoinColumn(name = "id_requisito")
    )
    private List<Requisito> requisitos;

    @ManyToMany
    @JoinTable(
        name = "sesion_especialidad",
        joinColumns = @JoinColumn(name = "id_sesion"),
        inverseJoinColumns = @JoinColumn(name = "id_especialidad")
    )
    private List<Especialidad> especialidades;
}
