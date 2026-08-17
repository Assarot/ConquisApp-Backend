package com.conquistadores.gestionclub.modules.avances.repository;

import com.conquistadores.gestionclub.modules.avances.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findBySesionIdSesion(Long idSesion);
    List<Asistencia> findByUsuarioIdUsuario(Long idUsuario);
}
