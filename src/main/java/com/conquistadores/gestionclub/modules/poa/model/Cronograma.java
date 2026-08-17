package com.conquistadores.gestionclub.modules.poa.model;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cronogramas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cronograma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cronograma")
    private Long idCronograma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase", nullable = false)
    private Clase clase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad")
    private ActividadPoa actividad;

    @Column(name = "plantilla_base")
    private String plantillaBase;
}
