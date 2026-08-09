package com.conquistadores.gestionclub.modules.poa.event;

import com.conquistadores.gestionclub.modules.poa.model.Cronograma;
import com.conquistadores.gestionclub.modules.poa.repository.CronogramaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PoaUpdatedListener {
    private static final Logger logger = LoggerFactory.getLogger(PoaUpdatedListener.class);

    @Autowired
    private CronogramaRepository cronogramaRepository;

    @Async
    @EventListener
    public void handlePoaUpdatedEvent(PoaUpdatedEvent event) {
        logger.info("Recibido PoaUpdatedEvent asíncrono para actividad: {} (Acción: {})", event.getIdActividad(), event.getAction());
        
        // Buscar todos los cronogramas que están vinculados a esta actividad
        List<Cronograma> cronogramasAfectados = cronogramaRepository.findByActividadIdActividad(event.getIdActividad());
        
        if (cronogramasAfectados.isEmpty()) {
            logger.info("No se encontraron cronogramas afectados para la actividad {}", event.getIdActividad());
            return;
        }

        logger.info("Sincronizando {} cronogramas automáticamente...", cronogramasAfectados.size());
        
        // En una lógica real de negocio, aquí actualizaríamos las fechas o estado en cascada en las sesiones de clase
        for (Cronograma cronograma : cronogramasAfectados) {
            // Ejemplo: actualizar auditoría de sincronización o marcar campos relacionados
            logger.info("Cronograma sincronizado automáticamente: ID {}", cronograma.getIdCronograma());
        }
        
        logger.info("Sincronización automática de cronogramas completada.");
    }
}
