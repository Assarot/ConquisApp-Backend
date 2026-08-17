package com.conquistadores.gestionclub.modules.notificaciones.controller;

import com.conquistadores.gestionclub.modules.notificaciones.model.Notificacion;
import com.conquistadores.gestionclub.modules.notificaciones.service.NotificacionService;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@CrossOrigin
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notificacion>> getNotificaciones(@PathVariable Long idUsuario) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long callerId = userDetails.getUsuario().getIdUsuario();
        String role = userDetails.getUsuario().getRol().getNombre().toUpperCase();

        if (!callerId.equals(idUsuario) && !"ADMINISTRADOR".equals(role)) {
            throw new RuntimeException("Acceso denegado: No puedes ver las notificaciones de otro usuario.");
        }

        return ResponseEntity.ok(notificacionService.getNotificacionesByUsuario(idUsuario));
    }

    @PutMapping("/{id}/leer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long callerId = userDetails.getUsuario().getIdUsuario();
        String role = userDetails.getUsuario().getRol().getNombre().toUpperCase();

        // Enforce notification ownership check
        Notificacion notificacion = notificacionService.marcarComoLeida(id);
        if (!notificacion.getUsuarioDestino().getIdUsuario().equals(callerId) && !"ADMINISTRADOR".equals(role)) {
            throw new RuntimeException("Acceso denegado: No puedes modificar notificaciones ajenas.");
        }

        return ResponseEntity.ok(notificacion);
    }
}
