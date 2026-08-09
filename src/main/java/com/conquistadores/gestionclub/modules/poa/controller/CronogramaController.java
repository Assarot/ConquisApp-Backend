package com.conquistadores.gestionclub.modules.poa.controller;

import com.conquistadores.gestionclub.modules.poa.model.Cronograma;
import com.conquistadores.gestionclub.modules.poa.model.BloqueCronograma;
import com.conquistadores.gestionclub.modules.poa.service.CronogramaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cronogramas")
@CrossOrigin
public class CronogramaController {

    @Autowired
    private CronogramaService cronogramaService;

    @GetMapping("/clase/{idClase}")
    public ResponseEntity<List<Cronograma>> getCronogramasByClase(@PathVariable String idClase) {
        return ResponseEntity.ok(cronogramaService.getCronogramasByClase(idClase));
    }

    @GetMapping("/{idCronograma}/bloques")
    public ResponseEntity<List<BloqueCronograma>> getBloquesByCronograma(@PathVariable String idCronograma) {
        return ResponseEntity.ok(cronogramaService.getBloquesByCronograma(idCronograma));
    }

    @PostMapping("/{idCronograma}/bloques")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<BloqueCronograma> registrarBloque(
            @PathVariable String idCronograma,
            @RequestBody BloqueCronograma bloque) {
        return ResponseEntity.ok(cronogramaService.registrarBloque(idCronograma, bloque));
    }

    @PutMapping("/bloques/{idBloque}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<BloqueCronograma> actualizarBloque(
            @PathVariable String idBloque,
            @RequestBody BloqueCronograma bloque) {
        return ResponseEntity.ok(cronogramaService.actualizarBloque(idBloque, bloque));
    }

    @DeleteMapping("/bloques/{idBloque}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<Void> eliminarBloque(@PathVariable String idBloque) {
        cronogramaService.eliminarBloque(idBloque);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idCronograma}/importar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<String> importarCronograma(
            @PathVariable String idCronograma,
            @RequestParam("file") MultipartFile file) {
        cronogramaService.importarCronogramaCsv(idCronograma, file);
        return ResponseEntity.ok("Importación de cronograma completada con éxito.");
    }
}
