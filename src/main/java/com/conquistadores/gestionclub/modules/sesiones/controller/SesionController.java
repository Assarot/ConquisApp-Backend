package com.conquistadores.gestionclub.modules.sesiones.controller;

import com.conquistadores.gestionclub.modules.sesiones.model.Sesion;
import com.conquistadores.gestionclub.modules.sesiones.service.SesionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sesiones")
public class SesionController {

    @Autowired
    private SesionService sesionService;

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
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'INSTRUCTOR') and (#sesion.clase == null or @securityService.hasAccessToClase(#sesion.clase.idClase))")
    public ResponseEntity<Sesion> guardarSesion(@RequestBody Sesion sesion) {
        return ResponseEntity.ok(sesionService.guardarSesion(sesion));
    }
}
