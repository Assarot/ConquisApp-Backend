package com.conquistadores.gestionclub.modules.club.model;

import com.conquistadores.gestionclub.modules.auditoria.listener.AuditListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "clubes")
@EntityListeners(AuditListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_club")
    private Long idClub;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo; // "CONQUISTADORES" o "LIDERES"

    @Column
    private String region;

    @Column(columnDefinition = "TEXT")
    private String configuracion; // Almacena JSON string para parámetros dinámicos

    @Transient
    private Long miembrosCount = 0L;

    @Transient
    private Long unidadesCount = 0L;
}
