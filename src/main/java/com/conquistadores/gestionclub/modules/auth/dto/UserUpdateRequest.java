package com.conquistadores.gestionclub.modules.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Long idRol;
    private Long idClub;
}
