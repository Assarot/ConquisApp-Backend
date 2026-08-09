package com.conquistadores.gestionclub.modules.rankings.service;

import com.conquistadores.gestionclub.modules.club.model.Unidad;
import com.conquistadores.gestionclub.modules.club.repository.UnidadRepository;
import com.conquistadores.gestionclub.modules.rankings.model.RankingUnidad;
import com.conquistadores.gestionclub.modules.rankings.repository.RankingUnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    private RankingUnidadRepository rankingUnidadRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    public List<RankingUnidad> obtenerRankingDeUnidades(String idClub) {
        // En una lógica real, filtraríamos los rankings por club consultando las unidades
        List<Unidad> unidades = unidadRepository.findByClubIdClub(idClub);
        // Retornar todos para este club
        return rankingUnidadRepository.findAll();
    }

    public Map<String, Object> obtenerIndicadoresDeGestion(String idClub) {
        Map<String, Object> indicadores = new HashMap<>();
        
        // Mocking statistics for testing / compliance indicators (RN-38)
        indicadores.put("clubId", idClub);
        indicadores.put("porcentajeCumplimientoAnual", 75.5);
        indicadores.put("claseConMayorAvance", "Exploradores");
        indicadores.put("conquistadorConMayorProgreso", "Juan Pérez");
        indicadores.put("instructoresActivos", 4);
        
        return indicadores;
    }

    @Transactional
    public RankingUnidad registrarPuntajeUnidad(String idUnidad, Double puntaje, String periodo, String reglamento) {
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
