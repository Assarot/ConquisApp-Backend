package com.conquistadores.gestionclub.modules.materiales.repository;

import com.conquistadores.gestionclub.modules.materiales.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {
    List<Material> findByClaseIdClase(String idClase);
    List<Material> findByEspecialidadIdEspecialidad(String idEspecialidad);
}
