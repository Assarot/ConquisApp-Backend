package com.conquistadores.gestionclub.modules.poa.service;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.modules.poa.event.PoaUpdatedEvent;
import com.conquistadores.gestionclub.modules.poa.model.ActividadPoa;
import com.conquistadores.gestionclub.modules.poa.model.Poa;
import com.conquistadores.gestionclub.modules.poa.repository.ActividadPoaRepository;
import com.conquistadores.gestionclub.modules.poa.repository.PoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class PoaService {

    @Autowired
    private PoaRepository poaRepository;

    @Autowired
    private ActividadPoaRepository actividadPoaRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<Poa> getPoasByClub(String idClub) {
        return poaRepository.findByClubIdClub(idClub);
    }

    public List<ActividadPoa> getActividadesByPoa(String idPoa) {
        return actividadPoaRepository.findByPoaIdPoa(idPoa);
    }

    @Transactional
    public Poa crearPoa(String idClub, Integer anio) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));

        poaRepository.findByClubIdClubAndAnio(idClub, anio).ifPresent(p -> {
            throw new RuntimeException("El POA de este año ya existe para este club.");
        });

        Poa poa = new Poa();
        poa.setClub(club);
        poa.setAnio(anio);
        poa.setEstado("BORRADOR");
        return poaRepository.save(poa);
    }

    @Transactional
    public ActividadPoa registrarActividad(String idPoa, ActividadPoa nuevaActividad) {
        Poa poa = poaRepository.findById(idPoa)
                .orElseThrow(() -> new RuntimeException("POA no encontrado"));

        nuevaActividad.setPoa(poa);
        ActividadPoa saved = actividadPoaRepository.save(nuevaActividad);
        
        // Disparar evento para sincronizar cronogramas
        eventPublisher.publishEvent(new PoaUpdatedEvent(this, saved.getIdActividad(), "CREATE"));
        
        return saved;
    }

    @Transactional
    public ActividadPoa actualizarFechaActividad(String idActividad, LocalDate nuevaFecha) {
        ActividadPoa actividad = actividadPoaRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

        actividad.setFecha(nuevaFecha);
        ActividadPoa updated = actividadPoaRepository.save(actividad);

        // Disparar evento para sincronizar cronogramas automáticamente (RN-03)
        eventPublisher.publishEvent(new PoaUpdatedEvent(this, updated.getIdActividad(), "UPDATE_DATE"));

        return updated;
    }
}
