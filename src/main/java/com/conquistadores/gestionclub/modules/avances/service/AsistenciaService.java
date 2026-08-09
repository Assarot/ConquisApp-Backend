package com.conquistadores.gestionclub.modules.avances.service;

import com.conquistadores.gestionclub.modules.avances.model.Asistencia;
import com.conquistadores.gestionclub.modules.avances.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    public List<Asistencia> getAsistenciasBySesion(String idSesion) {
        return asistenciaRepository.findBySesionIdSesion(idSesion);
    }

    @Transactional
    public List<Asistencia> registrarAsistencias(List<Asistencia> asistencias) {
        return asistenciaRepository.saveAll(asistencias);
    }
}
