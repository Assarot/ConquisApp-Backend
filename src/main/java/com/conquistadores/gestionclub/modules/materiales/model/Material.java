package com.conquistadores.gestionclub.modules.materiales.model;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;
import com.conquistadores.gestionclub.modules.sesiones.model.Sesion;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "materiales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_material")
    private String idMaterial;

    @Column(nullable = false)
    private String tipo; // "PDF", "WORD", "IMAGEN", "ENLACE", "VIDEO"

    @Column(name = "url_o_archivo", nullable = false, length = 1000)
    private String urlOArchivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialidad")
    private Especialidad especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase")
    private Clase clase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sesion")
    private Sesion sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creador", nullable = false)
    private Usuario usuarioCreador;
}
