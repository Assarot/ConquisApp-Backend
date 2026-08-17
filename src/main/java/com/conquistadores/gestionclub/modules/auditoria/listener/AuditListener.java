package com.conquistadores.gestionclub.modules.auditoria.listener;

import com.conquistadores.gestionclub.config.SpringContext;
import com.conquistadores.gestionclub.modules.auditoria.model.Auditoria;
import com.conquistadores.gestionclub.modules.auditoria.repository.AuditoriaRepository;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

public class AuditListener {

    @PostPersist
    public void afterPersist(Object entity) {
        audit(entity, "CREATE", null, entity.toString());
    }

    @PostUpdate
    public void afterUpdate(Object entity) {
        audit(entity, "UPDATE", null, entity.toString());
    }

    @PostRemove
    public void afterRemove(Object entity) {
        audit(entity, "DELETE", entity.toString(), "ELIMINADO");
    }

    private void audit(Object entity, String accion, String valorAnterior, String valorNuevo) {
        String className = entity.getClass().getSimpleName();
        if ("Auditoria".equals(className) || "Notificacion".equals(className)) {
            return; // Avoid infinite loops!
        }

        try {
            AuditoriaRepository repo = SpringContext.getBean(AuditoriaRepository.class);
            UsuarioRepository userRepo = SpringContext.getBean(UsuarioRepository.class);

            var authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario responsable = null;
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                responsable = userDetails.getUsuario();
            }

            if (responsable == null) {
                // Fallback to first user in system if not authenticated
                responsable = userRepo.findAll().stream().findFirst().orElse(null);
            }

            if (responsable != null) {
                Auditoria aud = new Auditoria();
                aud.setUsuario(responsable);
                aud.setFechaHora(LocalDateTime.now());
                aud.setModulo(className.toUpperCase());
                aud.setAccion(accion + "_" + className.toUpperCase());
                aud.setValorAnterior(valorAnterior != null && valorAnterior.length() > 4000 ? valorAnterior.substring(0, 4000) : valorAnterior);
                aud.setValorNuevo(valorNuevo != null && valorNuevo.length() > 4000 ? valorNuevo.substring(0, 4000) : valorNuevo);
                repo.save(aud);
            }
        } catch (Exception e) {
            // Prevent audit failures from blocking business transactions
            System.err.println("Error saving audit log: " + e.getMessage());
        }
    }
}
