package com.conquistadores.gestionclub.modules.club.service;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    public List<Club> getAllClubes() {
        return clubRepository.findAll();
    }

    public Club getClubById(String idClub) {
        return clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado con ID: " + idClub));
    }

    public Club registrarClub(Club club) {
        return clubRepository.save(club);
    }

    public Club actualizarClub(String idClub, Club clubDetails) {
        Club existing = getClubById(idClub);
        existing.setNombre(clubDetails.getNombre());
        existing.setTipo(clubDetails.getTipo());
        existing.setConfiguracion(clubDetails.getConfiguracion());
        return clubRepository.save(existing);
    }

    public void eliminarClub(String idClub) {
        clubRepository.deleteById(idClub);
    }
}
