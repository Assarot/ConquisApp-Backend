package com.conquistadores.gestionclub.modules.rankings.controller;

import com.conquistadores.gestionclub.modules.rankings.model.RankingUnidad;
import com.conquistadores.gestionclub.modules.rankings.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/ranking/club/{idClub}")
    @PreAuthorize("@securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<List<RankingUnidad>> obtenerRankingDeUnidades(@PathVariable Long idClub) {
        return ResponseEntity.ok(reporteService.obtenerRankingDeUnidades(idClub));
    }

    @GetMapping("/indicadores/club/{idClub}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToClub(#idClub)")
    public ResponseEntity<Map<String, Object>> obtenerIndicadoresDeGestion(@PathVariable Long idClub) {
        return ResponseEntity.ok(reporteService.obtenerIndicadoresDeGestion(idClub));
    }

    @PostMapping("/ranking/unidad/{idUnidad}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToUnidad(#idUnidad)")
    public ResponseEntity<RankingUnidad> registrarPuntajeUnidad(
            @PathVariable Long idUnidad,
            @RequestParam Double puntaje,
            @RequestParam String periodo,
            @RequestParam String reglamento) {
        return ResponseEntity.ok(reporteService.registrarPuntajeUnidad(idUnidad, puntaje, periodo, reglamento));
    }
}
