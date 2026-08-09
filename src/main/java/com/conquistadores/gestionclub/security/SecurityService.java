package com.conquistadores.gestionclub.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    public boolean isInstructorOfClass(String idClase) {
        // En un caso real, consultaríamos la relación N:M instructor_clase en la BD.
        // Retornamos true temporalmente para validar el flujo.
        return true;
    }

    public boolean isConsejeroOfUnidad(String idUnidad) {
        // En un caso real, consultaríamos la relación consejero_unidad en la BD.
        // Retornamos true temporalmente para validar el flujo.
        return true;
    }
}
