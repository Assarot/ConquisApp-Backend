package com.conquistadores.gestionclub.modules.auditoria.model;

import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_auditoria")
    private String idAuditoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String modulo;

    @Column(nullable = false)
    private String accion;

    @Column(name = "valor_anterior", length = 4000)
    private String valorAnterior;

    @Column(name = "valor_nuevo", nullable = false, length = 4000)
    private String valorNuevo;
}
