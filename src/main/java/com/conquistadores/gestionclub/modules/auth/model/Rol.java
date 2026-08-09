package com.conquistadores.gestionclub.modules.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_rol")
    private String idRol;

    @Column(nullable = false, unique = true)
    private String nombre; // e.g. ADMINISTRADOR, DIRECTOR, INSTRUCTOR, CONQUISTADOR, PADRE
}
