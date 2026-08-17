package com.conquistadores.gestionclub.modules.especialidades.service;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;
import com.conquistadores.gestionclub.modules.especialidades.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private ClubRepository clubRepository;

    public List<Especialidad> getEspecialidadesByClub(Long idClub) {
        return especialidadRepository.findAll();
    }

    public List<Especialidad> getEspecialidadesByCategoria(Long idClub, String categoria) {
        return especialidadRepository.findByCategoriaNombreIgnoreCase(categoria);
    }

    @Transactional
    public Especialidad registrarEspecialidad(Long idClub, Especialidad especialidad) {
        if (idClub != null) {
            Club club = clubRepository.findById(idClub)
                    .orElseThrow(() -> new RuntimeException("Club no encontrado"));
            especialidad.setClub(club);
        }
        return especialidadRepository.save(especialidad);
    }

    @Transactional
    public Especialidad actualizarEspecialidad(Long idEspecialidad, Especialidad request) {
        Especialidad especialidad = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        especialidad.setNombre(request.getNombre());
        especialidad.setRequiereExamen(request.getRequiereExamen());
        especialidad.setCategoria(request.getCategoria());
        especialidad.setNivelDestreza(request.getNivelDestreza());
        especialidad.setAnoIntroduccion(request.getAnoIntroduccion());
        especialidad.setPuntosMaestria(request.getPuntosMaestria());
        especialidad.setDescripcion(request.getDescripcion());
        especialidad.setImagenUrl(request.getImagenUrl());

        return especialidadRepository.save(especialidad);
    }

    @Transactional
    public void eliminarEspecialidad(Long idEspecialidad) {
        Especialidad especialidad = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        especialidadRepository.delete(especialidad);
    }
}
