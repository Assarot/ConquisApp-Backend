package com.conquistadores.gestionclub.modules.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuariosAgrupadosDTO {
    private Long idClub;
    private String clubNombre;
    private List<UsuarioDTO> usuarios;
}
