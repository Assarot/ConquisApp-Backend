package com.conquistadores.gestionclub.modules.poa.controller;

import com.conquistadores.gestionclub.modules.poa.model.ActividadPoa;
import com.conquistadores.gestionclub.modules.poa.model.Poa;
import com.conquistadores.gestionclub.modules.poa.service.PoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/poa")
public class PoaController {

    @Autowired
    private PoaService poaService;

    @GetMapping("/club/{idClub}")
    @PreAuthorize("@securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<List<Poa>> getPoasByClub(@PathVariable Long idClub) {
        return ResponseEntity.ok(poaService.getPoasByClub(idClub));
    }

    @GetMapping("/{idPoa}/actividades")
    @PreAuthorize("@securityService.hasAccessToPoa(#idPoa)")
    public ResponseEntity<List<ActividadPoa>> getActividadesByPoa(@PathVariable Long idPoa) {
        return ResponseEntity.ok(poaService.getActividadesByPoa(idPoa));
    }

    @PostMapping("/club/{idClub}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<Poa> crearPoa(@PathVariable Long idClub, @RequestParam Integer anio) {
        return ResponseEntity.ok(poaService.crearPoa(idClub, anio));
    }

    @PostMapping("/{idPoa}/actividades")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToPoa(#idPoa)")
    public ResponseEntity<ActividadPoa> registrarActividad(@PathVariable Long idPoa, @RequestBody ActividadPoa nuevaActividad) {
        return ResponseEntity.ok(poaService.registrarActividad(idPoa, nuevaActividad));
    }

    @PutMapping("/actividades/{idActividad}/fecha")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToActividadPoa(#idActividad)")
    public ResponseEntity<ActividadPoa> actualizarFechaActividad(
            @PathVariable Long idActividad,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nuevaFecha) {
        return ResponseEntity.ok(poaService.actualizarFechaActividad(idActividad, nuevaFecha));
    }
}
