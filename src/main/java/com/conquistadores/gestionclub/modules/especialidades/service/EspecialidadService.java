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

    public List<Especialidad> getEspecialidadesByClub(String idClub) {
        return especialidadRepository.findByClubIdClub(idClub);
    }

    public List<Especialidad> getEspecialidadesByCategoria(String idClub, String categoria) {
        return especialidadRepository.findByClubIdClubAndCategoriaIgnoreCase(idClub, categoria);
    }

    @Transactional
    public Especialidad registrarEspecialidad(String idClub, Especialidad especialidad) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));

        especialidad.setClub(club);
        return especialidadRepository.save(especialidad);
    }

    @Transactional
    public Especialidad actualizarEspecialidad(String idEspecialidad, Especialidad request) {
        Especialidad especialidad = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        especialidad.setNombre(request.getNombre());
        especialidad.setRequiereExamen(request.getRequiereExamen());
        especialidad.setCategoria(request.getCategoria());
        especialidad.setPuntosMaestria(request.getPuntosMaestria());
        especialidad.setDescripcion(request.getDescripcion());
        especialidad.setImagenUrl(request.getImagenUrl());

        return especialidadRepository.save(especialidad);
    }

    @Transactional
    public void eliminarEspecialidad(String idEspecialidad) {
        Especialidad especialidad = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        especialidadRepository.delete(especialidad);
    }
}
