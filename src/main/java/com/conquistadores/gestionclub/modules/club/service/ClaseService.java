package com.conquistadores.gestionclub.modules.club.service;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClaseRepository;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClaseService {

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private ClubRepository clubRepository;

    public List<Clase> getClasesByClub(Long idClub) {
        return claseRepository.findAll();
    }

    public Clase getClaseById(Long idClase) {
        return claseRepository.findById(idClase)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada con ID: " + idClase));
    }

    @Transactional
    public Clase registrarClase(Long idClub, Clase clase) {
        if (idClub != null) {
            Club club = clubRepository.findById(idClub)
                    .orElseThrow(() -> new RuntimeException("Club no encontrado con ID: " + idClub));
            clase.setClub(club);
        }
        return claseRepository.save(clase);
    }

    @Transactional
    public Clase actualizarClase(Long idClase, Clase claseDetails) {
        Clase existing = getClaseById(idClase);
        existing.setNombre(claseDetails.getNombre());
        if (claseDetails.getVersionCuadernillo() != null) {
            existing.setVersionCuadernillo(claseDetails.getVersionCuadernillo());
        }
        return claseRepository.save(existing);
    }

    @Transactional
    public void eliminarClase(Long idClase) {
        Clase existing = getClaseById(idClase);
        claseRepository.delete(existing);
    }
}
