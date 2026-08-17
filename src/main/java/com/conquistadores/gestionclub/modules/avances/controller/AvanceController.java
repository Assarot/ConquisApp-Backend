package com.conquistadores.gestionclub.modules.avances.controller;

import com.conquistadores.gestionclub.modules.avances.model.Avance;
import com.conquistadores.gestionclub.modules.avances.service.AvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/avances")
public class AvanceController {

    @Autowired
    private AvanceService avanceService;

    @GetMapping("/miembro/{idMiembro}")
    @PreAuthorize("@securityService.hasAccessToMiembro(#idMiembro)")
    public ResponseEntity<List<Avance>> getAvancesByMiembro(@PathVariable Long idMiembro) {
        return ResponseEntity.ok(avanceService.getAvancesByMiembro(idMiembro));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'INSTRUCTOR') and (#avance.miembro == null or @securityService.hasAccessToMiembro(#avance.miembro.idMiembro))")
    public ResponseEntity<Avance> registrarAvance(@RequestBody Avance avance) {
        return ResponseEntity.ok(avanceService.registrarAvance(avance));
    }

    @PutMapping("/{id}/correccion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR') and @securityService.hasAccessToAvance(#id)")
    public ResponseEntity<Avance> corregirAvance(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(avanceService.corregirAvance(id, nuevoEstado));
    }
}
