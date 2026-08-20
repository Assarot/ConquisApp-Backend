package com.conquistadores.gestionclub.modules.avances.service;

import com.conquistadores.gestionclub.modules.avances.model.Asistencia;
import com.conquistadores.gestionclub.modules.avances.repository.AsistenciaRepository;
import com.conquistadores.gestionclub.modules.rankings.model.RankingUnidad;
import com.conquistadores.gestionclub.modules.rankings.repository.RankingUnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository;
import com.conquistadores.gestionclub.modules.club.model.Unidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private RankingUnidadRepository rankingUnidadRepository;

    @Autowired
    private MiembroRepository miembroRepository;

    public List<Asistencia> getAsistenciasBySesion(Long idSesion) {
        return asistenciaRepository.findBySesionIdSesion(idSesion);
    }

    @Transactional
    public List<Asistencia> registrarAsistencias(List<Asistencia> asistencias) {
        List<Asistencia> saved = asistenciaRepository.saveAll(asistencias);
        if (saved.isEmpty()) {
            return saved;
        }

        Long idSesion = saved.get(0).getSesion().getIdSesion();
        String periodo = "SESION_" + idSesion;

        java.util.Map<Unidad, Double> unitScores = new java.util.HashMap<>();

        for (Asistencia a : saved) {
            Miembro miembro = miembroRepository.findById(a.getMiembro().getIdMiembro()).orElse(null);
            if (miembro == null || miembro.getUnidad() == null) {
                continue;
            }

            Unidad unidad = miembro.getUnidad();
            double score = 0.0;
            if ("PRESENTE".equalsIgnoreCase(a.getEstado())) {
                score += 10.0;
                if (Boolean.TRUE.equals(a.getPanoleta())) {
                    score += 10.0;
                }
                if (Boolean.TRUE.equals(a.getBiblia())) {
                    score += 10.0;
                }
                if (Boolean.TRUE.equals(a.getAgua())) {
                    score += 10.0;
                }
                if (Boolean.TRUE.equals(a.getMateriales())) {
                    score += 10.0;
                }
                if (Boolean.TRUE.equals(a.getCuota())) {
                    score += 10.0;
                }
            } else {
                a.setPanoleta(false);
                a.setBiblia(false);
                a.setAgua(false);
                a.setMateriales(false);
                a.setCuota(false);
            }

            unitScores.put(unidad, unitScores.getOrDefault(unidad, 0.0) + score);
        }

        for (java.util.Map.Entry<Unidad, Double> entry : unitScores.entrySet()) {
            Unidad unidad = entry.getKey();
            Double score = entry.getValue();

            RankingUnidad ranking = rankingUnidadRepository.findByUnidadIdUnidad(unidad.getIdUnidad()).stream()
                    .filter(r -> periodo.equals(r.getPeriodo()))
                    .findFirst()
                    .orElse(null);

            if (ranking == null) {
                ranking = new RankingUnidad();
                ranking.setUnidad(unidad);
                ranking.setPeriodo(periodo);
                ranking.setReglamentoAplicado("Puntos acumulados por asistencia y materiales en la sesión " + idSesion);
            }
            ranking.setPuntaje(score);
            rankingUnidadRepository.save(ranking);
        }

        return saved;
    }

    public List<Asistencia> getAsistenciasByUnidad(Long idUnidad) {
        return asistenciaRepository.findByMiembroUnidadIdUnidad(idUnidad);
    }
}
