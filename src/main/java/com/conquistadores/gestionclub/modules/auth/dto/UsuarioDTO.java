package com.conquistadores.gestionclub.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private Long idClub;
    private String estado;
}
