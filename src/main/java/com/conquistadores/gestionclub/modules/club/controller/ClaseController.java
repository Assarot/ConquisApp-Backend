package com.conquistadores.gestionclub.modules.club.controller;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.club.service.ClaseService;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clases")
@CrossOrigin
public class ClaseController {

    @Autowired
    private ClaseService claseService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Clase>> getClases(@RequestParam(required = false) Long idClub) {
        if (idClub == null) {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userDetails.getUsuario().getClub() != null) {
                idClub = userDetails.getUsuario().getClub().getIdClub();
            }
        }
        return ResponseEntity.ok(claseService.getClasesByClub(idClub));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAccessToClase(#id)")
    public ResponseEntity<Clase> getClaseById(@PathVariable Long id) {
        return ResponseEntity.ok(claseService.getClaseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Clase> registrarClase(@RequestBody Clase clase) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long idClub = userDetails.getUsuario().getClub() != null ? userDetails.getUsuario().getClub().getIdClub() : null;
        return ResponseEntity.ok(claseService.registrarClase(idClub, clase));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Clase> actualizarClase(@PathVariable Long id, @RequestBody Clase clase) {
        return ResponseEntity.ok(claseService.actualizarClase(id, clase));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarClase(@PathVariable Long id) {
        claseService.eliminarClase(id);
        return ResponseEntity.noContent().build();
    }
}
