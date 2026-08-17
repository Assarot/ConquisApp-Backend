package com.conquistadores.gestionclub.modules.poa.event;

import com.conquistadores.gestionclub.modules.poa.model.ActividadPoa;
import com.conquistadores.gestionclub.modules.poa.model.Cronograma;
import com.conquistadores.gestionclub.modules.poa.repository.ActividadPoaRepository;
import com.conquistadores.gestionclub.modules.poa.repository.CronogramaRepository;
import com.conquistadores.gestionclub.modules.sesiones.model.Sesion;
import com.conquistadores.gestionclub.modules.sesiones.repository.SesionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class PoaUpdatedListener {
    private static final Logger logger = LoggerFactory.getLogger(PoaUpdatedListener.class);

    @Autowired
    private CronogramaRepository cronogramaRepository;

    @Autowired
    private ActividadPoaRepository actividadPoaRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Async
    @EventListener
    @Transactional
    public void handlePoaUpdatedEvent(PoaUpdatedEvent event) {
        logger.info("Recibido PoaUpdatedEvent asíncrono para actividad: {} (Acción: {})", event.getIdActividad(), event.getAction());

        ActividadPoa actividad = actividadPoaRepository.findById(event.getIdActividad()).orElse(null);
        if (actividad == null) {
            logger.warn("Actividad con ID {} no encontrada", event.getIdActividad());
            return;
        }

        // Buscar todos los cronogramas que están vinculados a esta actividad
        List<Cronograma> cronogramasAfectados = cronogramaRepository.findByActividadIdActividad(event.getIdActividad());

        if (cronogramasAfectados.isEmpty()) {
            logger.info("No se encontraron cronogramas afectados para la actividad {}", event.getIdActividad());
            return;
        }

        logger.info("Sincronizando {} cronogramas automáticamente al nuevo POA...", cronogramasAfectados.size());

        // Actualizar la fecha de las sesiones de clase correspondientes
        for (Cronograma cronograma : cronogramasAfectados) {
            if (cronograma.getClase() != null) {
                List<Sesion> sesiones = sesionRepository.findByClaseIdClase(cronograma.getClase().getIdClase());
                for (Sesion sesion : sesiones) {
                    // Si la sesión no coincide con la fecha de la actividad, la reprogramamos
                    if (!actividad.getFecha().equals(sesion.getFecha())) {
                        logger.info("Reprogramando sesión ID {} de la fecha {} a la fecha {} de la actividad POA",
                                sesion.getIdSesion(), sesion.getFecha(), actividad.getFecha());
                        sesion.setFecha(actividad.getFecha());
                        sesionRepository.save(sesion);
                    }
                }
            }
        }

        logger.info("Sincronización automática de cronogramas y sesiones completada.");
    }
}
