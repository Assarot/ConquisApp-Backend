package com.conquistadores.gestionclub.modules.miembros.model;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.club.model.Unidad;
import com.conquistadores.gestionclub.modules.auditoria.listener.AuditListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "miembros")
@EntityListeners(AuditListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Miembro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_miembro")
    private Long idMiembro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_club", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad", nullable = true)
    private Unidad unidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase", nullable = false)
    private Clase clase;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String funcion; // e.g. DIRECTOR, SECRETARIO, INSTRUCTOR, CONSEJERO, CONQUISTADOR

    @Column(nullable = false)
    private String estado; // "ACTIVO" o "INACTIVO"

    @Column(name = "estado_ficha_salud", nullable = false)
    private String estadoFichaSalud; // "ACTUALIZADA" o "PENDIENTE"

    @Column(name = "estado_seguro", nullable = false)
    private String estadoSeguro; // "POSEE_SEGURO" o "NO_POSEE_SEGURO"

    @Column(name = "estado_adhesion_padres", nullable = false)
    private String estadoAdhesionPadres; // "FIRMADA" o "PENDIENTE"

    @Column(nullable = false)
    private Integer pendientes; // Cantidad calculada de pendientes (0 a 5)
}
