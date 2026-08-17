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
    @PreAuthorize("#idClub == null or @securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<List<UnidadResponse>> getUnidades(@RequestParam(required = false) Long idClub) {
        if (idClub == null) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails.getUsuario().getClub() != null) {
                idClub = userDetails.getUsuario().getClub().getIdClub();
            }
        }
        if (idClub == null) {
            return ResponseEntity.ok(unidadService.getAllUnidades());
        }
        return ResponseEntity.ok(unidadService.getUnidadesByClub(idClub));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Unidad> crearUnidad(@RequestBody Unidad request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long idClub = userDetails.getUsuario().getClub() != null ? userDetails.getUsuario().getClub().getIdClub() : request.getClub() != null ? request.getClub().getIdClub() : null;
        
        if (idClub == null) {
            throw new RuntimeException("Debes especificar un club para crear la unidad.");
        }
        
        Long idConsejero = request.getConsejero() != null ? request.getConsejero().getIdUsuario() : null;
        
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
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToUnidad(#id)")
    public ResponseEntity<Unidad> actualizarUnidad(@PathVariable Long id, @RequestBody Unidad request) {
        Long idConsejero = request.getConsejero() != null ? request.getConsejero().getIdUsuario() : null;
        
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
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToUnidad(#id)")
    public ResponseEntity<Void> eliminarUnidad(@PathVariable Long id) {
        unidadService.eliminarUnidad(id);
        return ResponseEntity.noContent().build();
    }
}
