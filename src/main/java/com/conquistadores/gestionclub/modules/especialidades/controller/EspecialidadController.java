package com.conquistadores.gestionclub.modules.especialidades.controller;

import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;
import com.conquistadores.gestionclub.modules.especialidades.service.EspecialidadService;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/especialidades")
@CrossOrigin
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Especialidad>> getEspecialidades(@RequestParam(required = false) Long idClub) {
        if (idClub == null) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails.getUsuario().getClub() != null) {
                idClub = userDetails.getUsuario().getClub().getIdClub();
            }
        }
        return ResponseEntity.ok(especialidadService.getEspecialidadesByClub(idClub));
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Especialidad>> getEspecialidadesByCategoria(
         @PathVariable String categoria,
         @RequestParam(required = false) Long idClub) {
        if (idClub == null) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails.getUsuario().getClub() != null) {
                idClub = userDetails.getUsuario().getClub().getIdClub();
            }
        }
        return ResponseEntity.ok(especialidadService.getEspecialidadesByCategoria(idClub, categoria));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Especialidad> registrarEspecialidad(@RequestBody Especialidad especialidad) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long idClub = userDetails.getUsuario().getClub() != null ? userDetails.getUsuario().getClub().getIdClub() : null;

        Especialidad nueva = especialidadService.registrarEspecialidad(idClub, especialidad);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Especialidad> actualizarEspecialidad(
            @PathVariable Long id,
            @RequestBody Especialidad especialidad) {
        Especialidad updated = especialidadService.actualizarEspecialidad(id, especialidad);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarEspecialidad(@PathVariable Long id) {
        especialidadService.eliminarEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}
