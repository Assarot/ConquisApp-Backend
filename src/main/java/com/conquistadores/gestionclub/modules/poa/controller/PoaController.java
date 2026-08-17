package com.conquistadores.gestionclub.modules.poa.controller;

import com.conquistadores.gestionclub.modules.poa.model.ActividadPoa;
import com.conquistadores.gestionclub.modules.poa.model.Poa;
import com.conquistadores.gestionclub.modules.poa.service.PoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/actividades/{idActividad}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'DIRECTOR_ASOCIADO')")
    public ResponseEntity<ActividadPoa> actualizarActividad(
            @PathVariable Long idActividad,
            @RequestBody ActividadPoa datos) {
        return ResponseEntity.ok(poaService.actualizarActividad(idActividad, datos));
    }

    @PostMapping("/{idPoa}/import-excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'DIRECTOR_ASOCIADO')")
    public ResponseEntity<?> importarDesdeExcel(
            @PathVariable Long idPoa,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
        }
        try {
            var importadas = poaService.importarDesdeExcel(idPoa, file);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Importación exitosa",
                "totalImportadas", importadas.size()
            ));
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/actividades/{idActividad}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToActividadPoa(#idActividad)")
    public ResponseEntity<Void> eliminarActividad(@PathVariable Long idActividad) {
        poaService.eliminarActividad(idActividad);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idPoa}/export-excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'DIRECTOR_ASOCIADO', 'INSTRUCTOR', 'CONSEJERO')")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Long idPoa) {
        try {
            byte[] excelBytes = poaService.exportarExcel(idPoa);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "POA-" + idPoa + ".xlsx");
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
