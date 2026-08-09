package com.conquistadores.gestionclub.modules.sesiones.service;

import com.conquistadores.gestionclub.modules.sesiones.model.Sesion;
import com.conquistadores.gestionclub.modules.sesiones.repository.SesionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SesionService {

    @Autowired
    private SesionRepository sesionRepository;

    public List<Sesion> getSesionesByClase(String idClase) {
        return sesionRepository.findByClaseIdClase(idClase);
    }

    public Optional<Sesion> getSesionById(String idSesion) {
        return sesionRepository.findById(idSesion);
    }

    @Transactional
    public Sesion guardarSesion(Sesion sesion) {
        return sesionRepository.save(sesion);
    }
}
