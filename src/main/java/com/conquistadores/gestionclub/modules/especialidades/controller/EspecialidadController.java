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
    public ResponseEntity<List<Especialidad>> getEspecialidades(@RequestParam(required = false) String idClub) {
        if (idClub == null || idClub.isEmpty()) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            idClub = userDetails.getUsuario().getClub().getIdClub();
        }
        return ResponseEntity.ok(especialidadService.getEspecialidadesByClub(idClub));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Especialidad>> getEspecialidadesByCategoria(
            @PathVariable String categoria,
            @RequestParam(required = false) String idClub) {
        if (idClub == null || idClub.isEmpty()) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            idClub = userDetails.getUsuario().getClub().getIdClub();
        }
        return ResponseEntity.ok(especialidadService.getEspecialidadesByCategoria(idClub, categoria));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<Especialidad> registrarEspecialidad(@RequestBody Especialidad especialidad) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idClub = userDetails.getUsuario().getClub().getIdClub();

        Especialidad nueva = especialidadService.registrarEspecialidad(idClub, especialidad);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<Especialidad> actualizarEspecialidad(
            @PathVariable String id,
            @RequestBody Especialidad especialidad) {
        Especialidad updated = especialidadService.actualizarEspecialidad(id, especialidad);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<Void> eliminarEspecialidad(@PathVariable String id) {
        especialidadService.eliminarEspecialidad(id);
        return ResponseEntity.noContent().build();
    }
}
