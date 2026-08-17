package com.conquistadores.gestionclub.modules.club.dto;

import com.conquistadores.gestionclub.modules.club.model.Club;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubConDirectorRequest {
    private Club club;
    
    // Si se asigna un director existente
    private Long idDirectorExistente;
    
    // Si se crea un director nuevo
    private String directorNombre;
    private String directorApellido;
    private String directorEmail;
    private String directorPassword;
}
