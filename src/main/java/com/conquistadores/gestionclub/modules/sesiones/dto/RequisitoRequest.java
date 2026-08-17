package com.conquistadores.gestionclub.modules.sesiones.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequisitoRequest {
    private String descripcion;
    private Long idClase;
    private Long idEspecialidad;
    private Boolean esAvanzado;
    private Long idCategoria;
}
