package com.conquistadores.gestionclub.modules.avances.model;

import com.conquistadores.gestionclub.modules.sesiones.model.Sesion;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auditoria.listener.AuditListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "asistencias")
@EntityListeners(AuditListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAsistencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sesion", nullable = false)
    private Sesion sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario; // Conquistador, Instructor, Consejero, Líder

    @Column(nullable = false)
    private String estado; // "PRESENTE", "AUSENTE", "JUSTIFICADO"

    @Column(name = "panoleta", nullable = false, columnDefinition = "boolean default false")
    private Boolean panoleta = false;

    @Column(name = "biblia", nullable = false, columnDefinition = "boolean default false")
    private Boolean biblia = false;

    @Column(name = "agua", nullable = false, columnDefinition = "boolean default false")
    private Boolean agua = false;

    @Column(name = "materiales", nullable = false, columnDefinition = "boolean default false")
    private Boolean materiales = false;

    @Column(name = "cuota", nullable = false, columnDefinition = "boolean default false")
    private Boolean cuota = false;
}
