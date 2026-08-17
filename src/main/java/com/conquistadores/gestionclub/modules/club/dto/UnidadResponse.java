package com.conquistadores.gestionclub.modules.club.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnidadResponse {
    private Long idUnidad;
    private String nombre;
    private Long consejeroId;
    private String consejeroNombre;
    private Long miembrosCount;
    private Double puntos;
    private String icono;
    private String color;
    private String descripcion;
}
