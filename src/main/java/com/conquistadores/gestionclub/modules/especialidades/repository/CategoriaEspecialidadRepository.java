package com.conquistadores.gestionclub.modules.especialidades.repository;

import com.conquistadores.gestionclub.modules.especialidades.model.CategoriaEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaEspecialidadRepository extends JpaRepository<CategoriaEspecialidad, Long> {
    Optional<CategoriaEspecialidad> findByNombreIgnoreCase(String nombre);
}
