package com.conquistadores.gestionclub.modules.avances.model;

import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.sesiones.model.Requisito;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auditoria.listener.AuditListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "avances")
@EntityListeners(AuditListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Avance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avance")
    private Long idAvance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_requisito", nullable = false)
    private Requisito requisito;

    @Column(nullable = false)
    private String estado; // "PENDIENTE", "EN_PROGRESO", "COMPLETADO"

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instructor_responsable")
    private Usuario instructorResponsable;
}
