package com.conquistadores.gestionclub.modules.poa.repository;

import com.conquistadores.gestionclub.modules.poa.model.Cronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CronogramaRepository extends JpaRepository<Cronograma, Long> {
    List<Cronograma> findByClaseIdClase(Long idClase);
    List<Cronograma> findByActividadIdActividad(Long idActividad);
}
