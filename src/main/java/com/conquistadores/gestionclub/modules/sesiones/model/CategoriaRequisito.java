package com.conquistadores.gestionclub.modules.sesiones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "categorias_requisitos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequisito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_categoria")
    private String idCategoria;

    @Column(nullable = false)
    private String nombre;
}
