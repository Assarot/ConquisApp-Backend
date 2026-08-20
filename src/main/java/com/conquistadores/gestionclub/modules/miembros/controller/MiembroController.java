package com.conquistadores.gestionclub.modules.miembros.controller;

import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.miembros.service.MiembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/miembros")
public class MiembroController {

    @Autowired
    private MiembroService miembroService;

    @GetMapping("/club/{idClub}")
    @PreAuthorize("@securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<List<Miembro>> getMiembrosByClub(@PathVariable Long idClub) {
        return ResponseEntity.ok(miembroService.getMiembrosByClub(idClub));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAccessToMiembro(#id)")
    public ResponseEntity<Miembro> getMiembroById(@PathVariable Long id) {
        return miembroService.getMiembroById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Miembro> registrarMiembro(@RequestBody Miembro miembro) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long callerClubId = userDetails.getUsuario().getClub().getIdClub();
        String role = userDetails.getUsuario().getRol().getNombre().toUpperCase();

        if (!"ADMINISTRADOR".equals(role)) {
            if (miembro.getClub() == null || !miembro.getClub().getIdClub().equals(callerClubId)) {
                throw new RuntimeException("Acceso denegado: No puedes registrar miembros en otros clubes.");
            }
        }
        return ResponseEntity.ok(miembroService.registrarMiembro(miembro));
    }

    @PutMapping("/{id}/unidad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToMiembro(#id) and @securityService.hasAccessToUnidad(#idUnidadDestino)")
    public ResponseEntity<Miembro> cambiarUnidad(@PathVariable Long id, @RequestParam Long idUnidadDestino) {
        return ResponseEntity.ok(miembroService.cambiarUnidad(id, idUnidadDestino));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToMiembro(#id)")
    public ResponseEntity<Miembro> inactivarMiembro(@PathVariable Long id) {
        return ResponseEntity.ok(miembroService.inactivarMiembro(id));
    }

    @PostMapping("/importar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<String> importarMiembros(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idClub") Long idClub) {
        miembroService.importarMiembrosExcel(file, idClub);
        return ResponseEntity.ok("Importación masiva de miembros completada con éxito.");
    }

    @GetMapping("/club/{idClub}/exportar-excel")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'DIRECTOR_ASOCIADO') and @securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Long idClub) {
        try {
            byte[] excelBytes = miembroService.exportarExcel(idClub);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "Miembros-Club-" + idClub + ".xlsx");
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
