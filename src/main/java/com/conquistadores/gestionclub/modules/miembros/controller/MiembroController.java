package com.conquistadores.gestionclub.modules.miembros.controller;

import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.miembros.service.MiembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/miembros")
public class MiembroController {

    @Autowired
    private MiembroService miembroService;

    @GetMapping("/club/{idClub}")
    public ResponseEntity<List<Miembro>> getMiembrosByClub(@PathVariable String idClub) {
        return ResponseEntity.ok(miembroService.getMiembrosByClub(idClub));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Miembro> getMiembroById(@PathVariable String id) {
        return miembroService.getMiembroById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Miembro> registrarMiembro(@RequestBody Miembro miembro) {
        return ResponseEntity.ok(miembroService.registrarMiembro(miembro));
    }

    @PutMapping("/{id}/unidad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Miembro> cambiarUnidad(@PathVariable String id, @RequestParam String idUnidadDestino) {
        return ResponseEntity.ok(miembroService.cambiarUnidad(id, idUnidadDestino));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Miembro> inactivarMiembro(@PathVariable String id) {
        return ResponseEntity.ok(miembroService.inactivarMiembro(id));
    }

    @PostMapping("/importar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<String> importarMiembros(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idClub") String idClub) {
        miembroService.importarMiembrosCsv(file, idClub);
        return ResponseEntity.ok("Importación masiva de miembros completada con éxito.");
    }
}
