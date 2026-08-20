package com.conquistadores.gestionclub.modules.avances.controller;

import com.conquistadores.gestionclub.modules.avances.model.Asistencia;
import com.conquistadores.gestionclub.modules.avances.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @GetMapping("/sesion/{idSesion}")
    @PreAuthorize("@securityService.hasAccessToSesion(#idSesion)")
    public ResponseEntity<List<Asistencia>> getAsistenciasBySesion(@PathVariable Long idSesion) {
        return ResponseEntity.ok(asistenciaService.getAsistenciasBySesion(idSesion));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'INSTRUCTOR', 'CONSEJERO') and (#asistencias.isEmpty() or @securityService.hasAccessToSesion(#asistencias[0].sesion.idSesion))")
    public ResponseEntity<List<Asistencia>> registrarAsistencias(@RequestBody List<Asistencia> asistencias) {
        return ResponseEntity.ok(asistenciaService.registrarAsistencias(asistencias));
    }

    @GetMapping("/unidad/{idUnidad}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'CONSEJERO', 'DIRECTOR_ASOCIADO', 'INSTRUCTOR') and @securityService.hasAccessToUnidad(#idUnidad)")
    public ResponseEntity<List<Asistencia>> getAsistenciasByUnidad(@PathVariable Long idUnidad) {
        return ResponseEntity.ok(asistenciaService.getAsistenciasByUnidad(idUnidad));
    }
}
