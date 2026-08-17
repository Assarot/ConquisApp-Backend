package com.conquistadores.gestionclub.modules.miembros.repository;

import com.conquistadores.gestionclub.modules.miembros.model.HistorialUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialUnidadRepository extends JpaRepository<HistorialUnidad, Long> {
    List<HistorialUnidad> findByMiembroIdMiembro(Long idMiembro);
}
