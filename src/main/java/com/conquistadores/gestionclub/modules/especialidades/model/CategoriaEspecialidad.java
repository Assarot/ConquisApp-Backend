package com.conquistadores.gestionclub.modules.especialidades.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "categorias_especialidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaEspecialidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria_especialidad")
    private Long idCategoriaEspecialidad;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "tiene_maestria", nullable = false)
    private Boolean tieneMaestria;
}
