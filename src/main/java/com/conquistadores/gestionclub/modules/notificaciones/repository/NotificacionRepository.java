package com.conquistadores.gestionclub.modules.notificaciones.repository;

import com.conquistadores.gestionclub.modules.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, String> {
    List<Notificacion> findByUsuarioDestinoIdUsuarioAndLeidoFalse(String idUsuario);
}
