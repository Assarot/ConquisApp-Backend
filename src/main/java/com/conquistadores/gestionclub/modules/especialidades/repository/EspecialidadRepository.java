package com.conquistadores.gestionclub.modules.especialidades.repository;

import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, String> {
    List<Especialidad> findByClubIdClub(String idClub);
    List<Especialidad> findByClubIdClubAndCategoriaIgnoreCase(String idClub, String categoria);
}
