package com.conquistadores.gestionclub.modules.materiales.repository;

import com.conquistadores.gestionclub.modules.materiales.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByClaseIdClaseAndUsuarioCreadorClubIdClub(Long idClase, Long idClub);
    List<Material> findByEspecialidadIdEspecialidadAndUsuarioCreadorClubIdClub(Long idEspecialidad, Long idClub);
    List<Material> findByClaseIdClase(Long idClase);
    List<Material> findByEspecialidadIdEspecialidad(Long idEspecialidad);
}
