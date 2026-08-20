package com.conquistadores.gestionclub.security;

import com.conquistadores.gestionclub.modules.club.repository.ClaseRepository;
import com.conquistadores.gestionclub.modules.club.repository.UnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository;
import com.conquistadores.gestionclub.modules.poa.repository.PoaRepository;
import com.conquistadores.gestionclub.modules.poa.repository.ActividadPoaRepository;
import com.conquistadores.gestionclub.modules.poa.repository.CronogramaRepository;
import com.conquistadores.gestionclub.modules.poa.repository.BloqueCronogramaRepository;
import com.conquistadores.gestionclub.modules.sesiones.repository.SesionRepository;
import com.conquistadores.gestionclub.modules.avances.repository.AvanceRepository;
import com.conquistadores.gestionclub.modules.materiales.repository.MaterialRepository;
import com.conquistadores.gestionclub.modules.especialidades.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("securityService")
public class SecurityService {

    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private PoaRepository poaRepository;

    @Autowired
    private ActividadPoaRepository actividadPoaRepository;

    @Autowired
    private CronogramaRepository cronogramaRepository;

    @Autowired
    private BloqueCronogramaRepository bloqueCronogramaRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private AvanceRepository avanceRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    private CustomUserDetails getCurrentUserDetails() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public boolean hasAccessToClub(Long idClub) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return user.getUsuario().getClub() != null && user.getUsuario().getClub().getIdClub().equals(idClub);
    }

    public boolean hasAccessToClase(Long idClase) {
        CustomUserDetails user = getCurrentUserDetails();
        return user != null;
    }

    public boolean hasAccessToUnidad(Long idUnidad) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return unidadRepository.findById(idUnidad)
                .map(u -> u.getClub() != null && u.getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub()))
                .orElse(false);
    }

    public boolean hasAccessToMiembro(Long idMiembro) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return miembroRepository.findById(idMiembro)
                .map(m -> m.getClub() != null && m.getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub()))
                .orElse(false);
    }

    public boolean hasAccessToPoa(Long idPoa) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        if (user.getUsuario().getClub() == null) {
            log.warn("hasAccessToPoa: user {} has no club", user.getUsername());
            return false;
        }
        Long userClubId = user.getUsuario().getClub().getIdClub();
        boolean result = poaRepository.existsByIdPoaAndClubIdClub(idPoa, userClubId);
        log.warn("hasAccessToPoa: idPoa={}, userClubId={}, result={}", idPoa, userClubId, result);
        return result;
    }

    public boolean hasAccessToActividadPoa(Long idActividad) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        if (user.getUsuario().getClub() == null) return false;
        return actividadPoaRepository.existsByIdActividadAndPoaClubIdClub(
                idActividad, user.getUsuario().getClub().getIdClub());
    }

    public boolean hasAccessToCronograma(Long idCronograma) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return cronogramaRepository.findById(idCronograma)
                .map(c -> c.getClase() != null && c.getClase().getClub() != null && c.getClase().getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub()))
                .orElse(false);
    }

    public boolean hasAccessToBloqueCronograma(Long idBloque) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return bloqueCronogramaRepository.findById(idBloque)
                .map(b -> b.getCronograma() != null && b.getCronograma().getClase() != null && b.getCronograma().getClase().getClub() != null && b.getCronograma().getClase().getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub()))
                .orElse(false);
    }

    public boolean hasAccessToSesion(Long idSesion) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return sesionRepository.findById(idSesion)
                .map(s -> s.getInstructor() != null && s.getInstructor().getClub() != null && s.getInstructor().getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub()))
                .orElse(false);
    }

    public boolean hasAccessToAvance(Long idAvance) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return avanceRepository.findById(idAvance)
                .map(a -> a.getMiembro() != null && a.getMiembro().getClub() != null && a.getMiembro().getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub()))
                .orElse(false);
    }

    public boolean hasAccessToMaterial(Long idMaterial) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return materialRepository.findById(idMaterial)
                .map(m -> m.getUsuarioCreador() != null && m.getUsuarioCreador().getClub() != null && m.getUsuarioCreador().getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub()))
                .orElse(false);
    }

    public boolean hasAccessToEspecialidad(Long idEspecialidad) {
        CustomUserDetails user = getCurrentUserDetails();
        return user != null;
    }

    public boolean isInstructorOfClass(Long idClase) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        String role = user.getUsuario().getRol().getNombre().toUpperCase();
        return ("INSTRUCTOR".equals(role) || "DIRECTOR".equals(role)) && hasAccessToClase(idClase);
    }

    public boolean isConsejeroOfUnidad(Long idUnidad) {
        CustomUserDetails user = getCurrentUserDetails();
        if (user == null) return false;
        if ("ADMINISTRADOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre())) {
            return true;
        }
        return unidadRepository.findById(idUnidad)
                .map(u -> {
                    boolean consejeroMatch = u.getConsejero() != null && u.getConsejero().getIdUsuario().equals(user.getUsuario().getIdUsuario());
                    boolean directorMatch = "DIRECTOR".equalsIgnoreCase(user.getUsuario().getRol().getNombre());
                    return (consejeroMatch || directorMatch) && u.getClub() != null && u.getClub().getIdClub().equals(user.getUsuario().getClub().getIdClub());
                })
                .orElse(false);
    }
}
