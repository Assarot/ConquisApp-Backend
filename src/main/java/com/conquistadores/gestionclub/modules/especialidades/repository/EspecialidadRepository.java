package com.conquistadores.gestionclub.modules.especialidades.repository;

import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    List<Especialidad> findByClubIdClub(Long idClub);
    List<Especialidad> findByCategoriaNombreIgnoreCase(String nombre);
    Optional<Especialidad> findByNombreIgnoreCase(String nombre);
}
