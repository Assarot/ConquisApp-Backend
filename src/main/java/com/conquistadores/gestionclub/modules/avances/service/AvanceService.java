package com.conquistadores.gestionclub.modules.avances.service;

import com.conquistadores.gestionclub.modules.auditoria.model.Auditoria;
import com.conquistadores.gestionclub.modules.auditoria.repository.AuditoriaRepository;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.modules.avances.model.Avance;
import com.conquistadores.gestionclub.modules.avances.repository.AvanceRepository;
import com.conquistadores.gestionclub.modules.notificaciones.service.NotificacionService;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvanceService {

    @Autowired
    private AvanceRepository avanceRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository miembroRepository;

    @Autowired
    private com.conquistadores.gestionclub.modules.sesiones.repository.RequisitoRepository requisitoRepository;

    @Transactional
    public List<Avance> getAvancesByMiembro(Long idMiembro) {
        com.conquistadores.gestionclub.modules.miembros.model.Miembro miembro = miembroRepository.findById(idMiembro)
                .orElseThrow(() -> new RuntimeException("Miembro no encontrado con ID: " + idMiembro));

        List<Avance> existentes = avanceRepository.findByMiembroIdMiembro(idMiembro);

        if (miembro.getClase() != null) {
            List<com.conquistadores.gestionclub.modules.sesiones.model.Requisito> requisitos =
                    requisitoRepository.findByClaseIdClase(miembro.getClase().getIdClase());

            boolean nuevoCreado = false;
            for (com.conquistadores.gestionclub.modules.sesiones.model.Requisito req : requisitos) {
                boolean existe = existentes.stream()
                        .anyMatch(a -> a.getRequisito().getIdRequisito().equals(req.getIdRequisito()));
                if (!existe) {
                    Avance nuevo = new Avance();
                    nuevo.setMiembro(miembro);
                    nuevo.setRequisito(req);
                    nuevo.setEstado("PENDIENTE");
                    nuevo.setFechaActualizacion(LocalDateTime.now());
                    avanceRepository.save(nuevo);
                    nuevoCreado = true;
                }
            }
            if (nuevoCreado) {
                existentes = avanceRepository.findByMiembroIdMiembro(idMiembro);
            }
        }
        return existentes;
    }

    @Transactional
    public Avance registrarAvance(Avance avance) {
        avance.setFechaActualizacion(LocalDateTime.now());
        return avanceRepository.save(avance);
    }

    @Transactional
    public Avance corregirAvance(Long idAvance, String nuevoEstado) {
        Avance avance = avanceRepository.findById(idAvance)
                .orElseThrow(() -> new RuntimeException("Avance no encontrado"));

        String estadoAnterior = avance.getEstado();
        avance.setEstado(nuevoEstado);
        avance.setFechaActualizacion(LocalDateTime.now());

        // Obtener usuario autenticado responsable de la modificación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario responsable = null;
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            responsable = userDetails.getUsuario();
        }

        // Si responsable es nulo (por pruebas o cargas de sistema), asignar un administrador global si existe
        if (responsable == null) {
            List<Usuario> admins = usuarioRepository.findAll();
            if (!admins.isEmpty()) {
                responsable = admins.get(0);
            } else {
                throw new RuntimeException("No se puede registrar auditoría sin un usuario responsable.");
            }
        }

        Avance updated = avanceRepository.save(avance);

        // Registrar acción en Auditoría (RN-26, RN-37, RN-42)
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(responsable);
        auditoria.setFechaHora(LocalDateTime.now());
        auditoria.setModulo("AVANCES");
        auditoria.setAccion("CORRECCION_AVANCE");
        auditoria.setValorAnterior("Estado anterior: " + estadoAnterior);
        auditoria.setValorNuevo("Nuevo estado: " + nuevoEstado);
        auditoriaRepository.save(auditoria);

        // Trigger notification to counselor if exists
        if (avance.getMiembro() != null && avance.getMiembro().getUnidad() != null && avance.getMiembro().getUnidad().getConsejero() != null) {
            notificacionService.registrarNotificacion(
                avance.getMiembro().getUnidad().getConsejero().getIdUsuario(),
                "Se ha modificado el progreso académico de " + avance.getMiembro().getNombre() + " " + avance.getMiembro().getApellido() + " a: " + nuevoEstado + "."
            );
        }

        return updated;
    }
}
