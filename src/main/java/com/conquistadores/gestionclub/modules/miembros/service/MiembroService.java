package com.conquistadores.gestionclub.modules.miembros.service;

import com.opencsv.CSVReader;
import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.model.Unidad;
import com.conquistadores.gestionclub.modules.club.repository.ClaseRepository;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.modules.club.repository.UnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.model.HistorialUnidad;
import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.miembros.repository.HistorialUnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository;
import com.conquistadores.gestionclub.modules.notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MiembroService {

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private HistorialUnidadRepository historialUnidadRepository;

    @Autowired
    private NotificacionService notificacionService;

    public List<Miembro> getMiembrosByClub(Long idClub) {
        return miembroRepository.findByClubIdClub(idClub);
    }

    public Optional<Miembro> getMiembroById(Long idMiembro) {
        return miembroRepository.findById(idMiembro);
    }

    @Transactional
    public Miembro registrarMiembro(Miembro miembro) {
        // Inicializar pendientes por defecto (RN-15)
        calcularPendientes(miembro);
        return miembroRepository.save(miembro);
    }

    @Transactional
    public Miembro cambiarUnidad(Long idMiembro, Long idUnidadDestino) {
        Miembro miembro = miembroRepository.findById(idMiembro)
                .orElseThrow(() -> new RuntimeException("Miembro no encontrado"));

        Unidad unidadDestino = unidadRepository.findById(idUnidadDestino)
                .orElseThrow(() -> new RuntimeException("Unidad destino no encontrada"));

        Unidad unidadOrigen = miembro.getUnidad();
        
        // Actualizar unidad del miembro
        miembro.setUnidad(unidadDestino);
        Miembro updated = miembroRepository.save(miembro);

        // Guardar registro en historial (RN-22)
        HistorialUnidad historial = new HistorialUnidad();
        historial.setMiembro(updated);
        historial.setUnidadOrigen(unidadOrigen);
        historial.setUnidadDestino(unidadDestino);
        historial.setFechaCambio(LocalDateTime.now());
        historialUnidadRepository.save(historial);

        // Trigger notification to the new Unit's Counselor if exists
        if (unidadDestino.getConsejero() != null) {
            notificacionService.registrarNotificacion(
                unidadDestino.getConsejero().getIdUsuario(),
                "El miembro " + miembro.getNombre() + " " + miembro.getApellido() + " ha sido asignado a tu unidad: " + unidadDestino.getNombre() + "."
            );
        }

        return updated;
    }

    @Transactional
    public Miembro inactivarMiembro(Long idMiembro) {
        Miembro miembro = miembroRepository.findById(idMiembro)
                .orElseThrow(() -> new RuntimeException("Miembro no encontrado"));

        // Los miembros nunca se eliminan físicamente del sistema (RN-20, RN-24)
        miembro.setEstado("INACTIVO");
        return miembroRepository.save(miembro);
    }

    @Transactional
    public void importarMiembrosCsv(MultipartFile file, Long idClub) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));

        // Cargar primera unidad y clase por defecto para nuevos miembros
        List<Unidad> unidades = unidadRepository.findByClubIdClub(idClub);
        List<Clase> clases = claseRepository.findByClubIdClub(idClub);

        if (unidades.isEmpty() || clases.isEmpty()) {
            throw new RuntimeException("El club debe tener al menos una unidad y una clase antes de importar miembros.");
        }

        Unidad unidadDefecto = unidades.get(0);
        Clase claseDefecto = clases.get(0);

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReader(reader);
            String[] nextLine;
            csvReader.readNext(); // Saltar cabecera
            
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length < 5) continue;
                String nombre = nextLine[0].trim();
                String apellido = nextLine[1].trim();
                String funcion = nextLine[2].trim();
                String estadoFichaSalud = nextLine[3].trim();
                String estadoSeguro = nextLine[4].trim();
                String estadoAdhesionPadres = nextLine.length > 5 ? nextLine[5].trim() : "PENDIENTE";

                // Buscar duplicados por nombre y apellido en el club para Upsert (RN-21)
                Optional<Miembro> miembroExistente = miembroRepository.findByClubIdClub(idClub).stream()
                        .filter(m -> m.getNombre().equalsIgnoreCase(nombre) && m.getApellido().equalsIgnoreCase(apellido))
                        .findFirst();

                Miembro miembro;
                if (miembroExistente.isPresent()) {
                    miembro = miembroExistente.get();
                } else {
                    miembro = new Miembro();
                    miembro.setClub(club);
                    miembro.setNombre(nombre);
                    miembro.setApellido(apellido);
                    miembro.setUnidad(unidadDefecto);
                    miembro.setClase(claseDefecto);
                }

                miembro.setFuncion(funcion);
                miembro.setEstado("ACTIVO");
                miembro.setEstadoFichaSalud(estadoFichaSalud);
                miembro.setEstadoSeguro(estadoSeguro);
                miembro.setEstadoAdhesionPadres(estadoAdhesionPadres);
                
                calcularPendientes(miembro);
                miembroRepository.save(miembro);
            }
        } catch (Exception e) {
            throw new RuntimeException("Fallo al importar archivo CSV: " + e.getMessage(), e);
        }
    }

    private void calcularPendientes(Miembro miembro) {
        int pendientesCount = 0;
        if ("PENDIENTE".equalsIgnoreCase(miembro.getEstadoFichaSalud())) pendientesCount++;
        if ("NO_POSEE_SEGURO".equalsIgnoreCase(miembro.getEstadoSeguro())) pendientesCount++;
        if ("PENDIENTE".equalsIgnoreCase(miembro.getEstadoAdhesionPadres())) pendientesCount++;
        
        // En una lógica real, validar si tiene clase asignada y especialidades registradas (RN-15)
        if (miembro.getClase() == null) pendientesCount++;
        
        miembro.setPendientes(pendientesCount);
    }
}
