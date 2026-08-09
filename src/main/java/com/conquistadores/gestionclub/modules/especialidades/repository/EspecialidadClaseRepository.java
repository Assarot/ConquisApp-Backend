package com.conquistadores.gestionclub.modules.especialidades.repository;

import com.conquistadores.gestionclub.modules.especialidades.model.EspecialidadClase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EspecialidadClaseRepository extends JpaRepository<EspecialidadClase, String> {
    List<EspecialidadClase> findByClaseIdClase(String idClase);
}
