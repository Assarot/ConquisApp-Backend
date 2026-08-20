package com.conquistadores.gestionclub.modules.club.service;

import com.conquistadores.gestionclub.modules.club.dto.UnidadResponse;
import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.model.Unidad;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.modules.club.repository.UnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository;
import com.conquistadores.gestionclub.modules.rankings.model.RankingUnidad;
import com.conquistadores.gestionclub.modules.rankings.repository.RankingUnidadRepository;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.miembros.model.HistorialUnidad;
import com.conquistadores.gestionclub.modules.miembros.repository.HistorialUnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnidadService {

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private RankingUnidadRepository rankingUnidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistorialUnidadRepository historialUnidadRepository;

    public List<UnidadResponse> getUnidadesByClub(Long idClub) {
        List<Unidad> unidades = unidadRepository.findByClubIdClub(idClub);
        return unidades.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<UnidadResponse> getAllUnidades() {
        return unidadRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public Unidad crearUnidad(Long idClub, String nombre, Long idConsejero, String icono, String color, String descripcion, String imagen) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));

        Unidad unidad = new Unidad();
        unidad.setClub(club);
        unidad.setNombre(nombre);
        unidad.setIcono(icono);
        unidad.setColor(color);
        unidad.setDescripcion(descripcion);
        unidad.setImagen(imagen);

        if (idConsejero != null) {
            Usuario consejero = usuarioRepository.findById(idConsejero)
                    .orElseThrow(() -> new RuntimeException("Consejero no encontrado"));
            unidad.setConsejero(consejero);
        }

        return unidadRepository.save(unidad);
    }

    @Transactional
    public Unidad actualizarUnidad(Long idUnidad, String nombre, Long idConsejero, String icono, String color, String descripcion, String imagen) {
        Unidad unidad = unidadRepository.findById(idUnidad)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

        unidad.setNombre(nombre);
        if (icono != null) unidad.setIcono(icono);
        if (color != null) unidad.setColor(color);
        if (descripcion != null) unidad.setDescripcion(descripcion);
        unidad.setImagen(imagen);

        if (idConsejero != null) {
            Usuario consejero = usuarioRepository.findById(idConsejero)
                    .orElseThrow(() -> new RuntimeException("Consejero no encontrado"));
            unidad.setConsejero(consejero);
        } else {
            unidad.setConsejero(null);
        }

        return unidadRepository.save(unidad);
    }

    @Transactional
    public void eliminarUnidad(Long idUnidad) {
        Unidad unidad = unidadRepository.findById(idUnidad)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

        // 1. Disassociate members
        List<Miembro> miembros = miembroRepository.findByUnidadIdUnidad(idUnidad);
        for (Miembro miembro : miembros) {
            miembro.setUnidad(null);
            miembroRepository.save(miembro);
        }

        // 2. Disassociate history
        List<HistorialUnidad> origenHistories = historialUnidadRepository.findByUnidadOrigenIdUnidad(idUnidad);
        for (HistorialUnidad h : origenHistories) {
            h.setUnidadOrigen(null);
            historialUnidadRepository.save(h);
        }

        List<HistorialUnidad> destinoHistories = historialUnidadRepository.findByUnidadDestinoIdUnidad(idUnidad);
        for (HistorialUnidad h : destinoHistories) {
            h.setUnidadDestino(null);
            historialUnidadRepository.save(h);
        }

        // 3. Delete unit rankings
        List<RankingUnidad> rankings = rankingUnidadRepository.findByUnidadIdUnidad(idUnidad);
        rankingUnidadRepository.deleteAll(rankings);

        // 4. Delete the unit itself
        unidadRepository.delete(unidad);
    }

    private UnidadResponse mapToResponse(Unidad unidad) {
        UnidadResponse res = new UnidadResponse();
        res.setIdUnidad(unidad.getIdUnidad());
        res.setNombre(unidad.getNombre());
        res.setIcono(unidad.getIcono());
        res.setColor(unidad.getColor());
        res.setDescripcion(unidad.getDescripcion());
        res.setImagen(unidad.getImagen());

        if (unidad.getConsejero() != null) {
            res.setConsejeroId(unidad.getConsejero().getIdUsuario());
            res.setConsejeroNombre(unidad.getConsejero().getNombre() + " " + unidad.getConsejero().getApellido());
        }

        long miembrosCount = miembroRepository.findByUnidadIdUnidad(unidad.getIdUnidad()).stream()
                .filter(m -> "ACTIVO".equalsIgnoreCase(m.getEstado()))
                .count();
        res.setMiembrosCount(miembrosCount);

        List<RankingUnidad> rankings = rankingUnidadRepository.findByUnidadIdUnidad(unidad.getIdUnidad());
        double puntos = rankings.stream().mapToDouble(RankingUnidad::getPuntaje).sum();
        res.setPuntos(puntos);

        return res;
    }
}
