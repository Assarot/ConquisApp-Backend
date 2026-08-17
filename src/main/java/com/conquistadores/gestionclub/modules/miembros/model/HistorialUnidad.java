package com.conquistadores.gestionclub.modules.miembros.model;

import com.conquistadores.gestionclub.modules.club.model.Unidad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_unidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialUnidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial_unidad")
    private Long idHistorialUnidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_origen", nullable = false)
    private Unidad unidadOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_destino", nullable = false)
    private Unidad unidadDestino;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;
}
