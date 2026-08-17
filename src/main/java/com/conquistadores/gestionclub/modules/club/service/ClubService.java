package com.conquistadores.gestionclub.modules.club.service;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.modules.club.repository.UnidadRepository;
import com.conquistadores.gestionclub.modules.club.dto.ClubConDirectorRequest;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.model.Rol;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.modules.auth.repository.RolRepository;
import com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository;
import com.conquistadores.gestionclub.modules.poa.repository.PoaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private PoaRepository poaRepository;

    public List<Club> getAllClubes() {
        List<Club> clubes = clubRepository.findAll();
        for (Club club : clubes) {
            club.setMiembrosCount((long) miembroRepository.findByClubIdClub(club.getIdClub()).size());
            club.setUnidadesCount((long) unidadRepository.findByClubIdClub(club.getIdClub()).size());
        }
        return clubes;
    }

    public Club getClubById(Long idClub) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado con ID: " + idClub));
        club.setMiembrosCount((long) miembroRepository.findByClubIdClub(club.getIdClub()).size());
        club.setUnidadesCount((long) unidadRepository.findByClubIdClub(club.getIdClub()).size());
        return club;
    }

    public Club registrarClub(Club club) {
        return clubRepository.save(club);
    }

    @Transactional
    public Club registrarClubConDirector(ClubConDirectorRequest request) {
        Club club = request.getClub();
        club = clubRepository.save(club);
        
        Usuario director = null;
        if (request.getIdDirectorExistente() != null) {
            director = usuarioRepository.findById(request.getIdDirectorExistente())
                    .orElseThrow(() -> new RuntimeException("Usuario director no encontrado"));
            director.setClub(club);
            Rol rolDirector = rolRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Rol director no encontrado"));
            director.setRol(rolDirector);
            usuarioRepository.save(director);
        } else if (request.getDirectorEmail() != null && !request.getDirectorEmail().trim().isEmpty()) {
            if (usuarioRepository.findByEmail(request.getDirectorEmail()).isPresent()) {
                throw new RuntimeException("El email del director ya está registrado.");
            }
            Rol rolDirector = rolRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Rol director no encontrado"));
            director = new Usuario();
            director.setNombre(request.getDirectorNombre());
            director.setApellido(request.getDirectorApellido());
            director.setEmail(request.getDirectorEmail());
            director.setPasswordHash(passwordEncoder.encode(request.getDirectorPassword()));
            director.setClub(club);
            director.setRol(rolDirector);
            director.setEstado("ACTIVO");
            usuarioRepository.save(director);
        }
        
        if (director != null) {
            String config = club.getConfiguracion();
            String directorName = director.getNombre() + " " + director.getApellido();
            if (config == null || config.trim().isEmpty() || "{}".equals(config)) {
                config = "{\"director\":\"" + directorName + "\"}";
            } else {
                if (config.contains("\"director\":")) {
                    config = config.replaceAll("\"director\"\\s*:\\s*\"[^\"]*\"", "\"director\":\"" + directorName + "\"");
                } else {
                    config = config.substring(0, config.lastIndexOf("}")) + ",\"director\":\"" + directorName + "\"}";
                }
            }
            club.setConfiguracion(config);
            club = clubRepository.save(club);
        }
        return club;
    }

    public Club actualizarClub(Long idClub, Club clubDetails) {
        Club existing = getClubById(idClub);
        existing.setNombre(clubDetails.getNombre());
        existing.setTipo(clubDetails.getTipo());
        existing.setRegion(clubDetails.getRegion());
        existing.setConfiguracion(clubDetails.getConfiguracion());
        return clubRepository.save(existing);
    }

    @Transactional
    public void eliminarClub(Long idClub) {
        // 1. Detach all users from this club (usuarios.id_club nullable)
        List<Usuario> usuarios = usuarioRepository.findByClubIdClub(idClub);
        for (Usuario u : usuarios) {
            u.setClub(null);
            usuarioRepository.save(u);
        }

        // 2. Delete POAs linked to this club
        poaRepository.deleteAll(poaRepository.findByClubIdClub(idClub));

        // 3. Delete members linked to this club
        miembroRepository.deleteAll(miembroRepository.findByClubIdClub(idClub));

        // 4. Delete units linked to this club
        unidadRepository.deleteAll(unidadRepository.findByClubIdClub(idClub));

        // 5. Now safe to delete the club itself
        clubRepository.deleteById(idClub);
    }
}
