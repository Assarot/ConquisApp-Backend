package com.conquistadores.gestionclub.modules.notificaciones.service;

import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.modules.notificaciones.model.Notificacion;
import com.conquistadores.gestionclub.modules.notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Notificacion> getNotificacionesByUsuario(Long idUsuario) {
        return notificacionRepository.findByUsuarioDestinoIdUsuarioAndLeidoFalse(idUsuario);
    }

    @Transactional
    public Notificacion registrarNotificacion(Long idUsuarioDestino, String mensaje) {
        Usuario usuarioDestino = usuarioRepository.findById(idUsuarioDestino)
                .orElseThrow(() -> new RuntimeException("Usuario destino no encontrado con ID: " + idUsuarioDestino));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioDestino(usuarioDestino);
        notificacion.setMensaje(mensaje);
        notificacion.setFecha(LocalDateTime.now());
        notificacion.setLeido(false);

        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public Notificacion marcarComoLeida(Long idNotificacion) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + idNotificacion));
        notificacion.setLeido(true);
        return notificacionRepository.save(notificacion);
    }
}
