package com.conquistadores.gestionclub.modules.sesiones.repository;

import com.conquistadores.gestionclub.modules.sesiones.model.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {
    List<Sesion> findByClaseIdClase(Long idClase);
}
