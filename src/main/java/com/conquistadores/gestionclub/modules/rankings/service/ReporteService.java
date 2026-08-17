package com.conquistadores.gestionclub.modules.rankings.service;

import com.conquistadores.gestionclub.modules.club.model.Unidad;
import com.conquistadores.gestionclub.modules.club.repository.UnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository;
import com.conquistadores.gestionclub.modules.avances.model.Avance;
import com.conquistadores.gestionclub.modules.avances.repository.AvanceRepository;
import com.conquistadores.gestionclub.modules.rankings.model.RankingUnidad;
import com.conquistadores.gestionclub.modules.rankings.repository.RankingUnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private RankingUnidadRepository rankingUnidadRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private AvanceRepository avanceRepository;

    public List<RankingUnidad> obtenerRankingDeUnidades(Long idClub) {
        List<Unidad> unidades = unidadRepository.findByClubIdClub(idClub);
        List<Long> unidadIds = unidades.stream()
                .map(Unidad::getIdUnidad)
                .collect(Collectors.toList());

        if (unidadIds.isEmpty()) {
            return new ArrayList<>();
        }

        return rankingUnidadRepository.findByUnidadIdUnidadIn(unidadIds);
    }

    public Map<String, Object> obtenerIndicadoresDeGestion(Long idClub) {
        Map<String, Object> indicadores = new HashMap<>();

        List<Miembro> miembrosActivos = miembroRepository.findByClubIdClub(idClub).stream()
                .filter(m -> "ACTIVO".equalsIgnoreCase(m.getEstado()))
                .collect(Collectors.toList());

        List<Unidad> unidades = unidadRepository.findByClubIdClub(idClub);

        long totalMiembros = miembrosActivos.size();
        long totalUnidades = unidades.size();

        // Calculate progress completion metrics
        long totalAvances = 0;
        long avancesAprobados = 0;
        Map<String, Long> aprobadosPorMiembro = new HashMap<>();
        Map<String, Long> aprobadosPorClase = new HashMap<>();

        for (Miembro miembro : miembrosActivos) {
            List<Avance> avances = avanceRepository.findByMiembroIdMiembro(miembro.getIdMiembro());
            totalAvances += avances.size();

            long aprobados = avances.stream()
                    .filter(a -> "APROBADO".equalsIgnoreCase(a.getEstado()) || "COMPLETADO".equalsIgnoreCase(a.getEstado()))
                    .count();
            avancesAprobados += aprobados;

            if (aprobados > 0) {
                String nombreCompleto = miembro.getNombre() + " " + miembro.getApellido();
                aprobadosPorMiembro.put(nombreCompleto, aprobados);

                if (miembro.getClase() != null) {
                    String claseNombre = miembro.getClase().getNombre();
                    aprobadosPorClase.put(claseNombre, aprobadosPorClase.getOrDefault(claseNombre, 0L) + aprobados);
                }
            }
        }

        double porcentajeCumplimiento = totalAvances > 0 ? (double) avancesAprobados / totalAvances * 100.0 : 0.0;

        // Round to 1 decimal place
        porcentajeCumplimiento = Math.round(porcentajeCumplimiento * 10.0) / 10.0;

        // Find conquistador with most progress
        String topConquistador = aprobadosPorMiembro.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");

        // Find class with most progress
        String topClase = aprobadosPorClase.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguna");

        indicadores.put("clubId", idClub);
        indicadores.put("totalMiembrosActivos", totalMiembros);
        indicadores.put("totalUnidades", totalUnidades);
        indicadores.put("porcentajeCumplimientoAnual", porcentajeCumplimiento > 0 ? porcentajeCumplimiento : 0.0);
        indicadores.put("claseConMayorAvance", topClase);
        indicadores.put("conquistadorConMayorProgreso", topConquistador);

        return indicadores;
    }

    @Transactional
    public RankingUnidad registrarPuntajeUnidad(Long idUnidad, Double puntaje, String periodo, String reglamento) {
        Unidad unidad = unidadRepository.findById(idUnidad)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

        RankingUnidad ranking = new RankingUnidad();
        ranking.setUnidad(unidad);
        ranking.setPuntaje(puntaje);
        ranking.setPeriodo(periodo);
        ranking.setReglamentoAplicado(reglamento);

        return rankingUnidadRepository.save(ranking);
    }
}
