package com.conquistadores.gestionclub.modules.club.controller;

import com.conquistadores.gestionclub.modules.club.dto.UnidadResponse;
import com.conquistadores.gestionclub.modules.club.model.Unidad;
import com.conquistadores.gestionclub.modules.club.service.UnidadService;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/unidades")
@CrossOrigin
public class UnidadController {

    @Autowired
    private UnidadService unidadService;

    @GetMapping
    public ResponseEntity<List<UnidadResponse>> getUnidades(@RequestParam(required = false) String idClub) {
        if (idClub == null || idClub.isEmpty()) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            idClub = userDetails.getUsuario().getClub().getIdClub();
        }
        return ResponseEntity.ok(unidadService.getUnidadesByClub(idClub));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Unidad> crearUnidad(@RequestBody Unidad request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String idClub = userDetails.getUsuario().getClub().getIdClub();
        
        String idConsejero = request.getConsejero() != null ? request.getConsejero().getIdUsuario() : null;
        
        Unidad nueva = unidadService.crearUnidad(
                idClub,
                request.getNombre(),
                idConsejero,
                request.getIcono(),
                request.getColor(),
                request.getDescripcion()
        );
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Unidad> actualizarUnidad(@PathVariable String id, @RequestBody Unidad request) {
        String idConsejero = request.getConsejero() != null ? request.getConsejero().getIdUsuario() : null;
        
        Unidad updated = unidadService.actualizarUnidad(
                id,
                request.getNombre(),
                idConsejero,
                request.getIcono(),
                request.getColor(),
                request.getDescripcion()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Void> eliminarUnidad(@PathVariable String id) {
        unidadService.eliminarUnidad(id);
        return ResponseEntity.noContent().build();
    }
}
