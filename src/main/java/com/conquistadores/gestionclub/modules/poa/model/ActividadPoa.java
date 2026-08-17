package com.conquistadores.gestionclub.modules.poa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "actividades_poa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActividadPoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Long idActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_poa", nullable = false)
    private Poa poa;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String ambito; // "CLUB", "IGLESIA", "REGION", "ASOCIACION", "RECURRENTE"

    private String responsable;
}
