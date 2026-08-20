package com.conquistadores.gestionclub.modules.sesiones.controller;

import com.conquistadores.gestionclub.modules.sesiones.model.Sesion;
import com.conquistadores.gestionclub.modules.sesiones.service.SesionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.conquistadores.gestionclub.modules.club.repository.ClaseRepository;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/sesiones")
public class SesionController {

    @Autowired
    private SesionService sesionService;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/clase/{idClase}")
    @PreAuthorize("@securityService.hasAccessToClase(#idClase)")
    public ResponseEntity<List<Sesion>> getSesionesByClase(@PathVariable Long idClase) {
        return ResponseEntity.ok(sesionService.getSesionesByClase(idClase));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAccessToSesion(#id)")
    public ResponseEntity<Sesion> getSesionById(@PathVariable Long id) {
        return sesionService.getSesionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'INSTRUCTOR', 'CONSEJERO')")
    public ResponseEntity<Sesion> guardarSesion(@RequestBody Sesion sesion) {
        if (sesion.getClase() == null && sesion.getIdClase() != null) {
            claseRepository.findById(sesion.getIdClase()).ifPresent(sesion::setClase);
        }
        if (sesion.getInstructor() == null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            usuarioRepository.findByEmail(email).ifPresent(sesion::setInstructor);
        }
        return ResponseEntity.ok(sesionService.guardarSesion(sesion));
    }
}
