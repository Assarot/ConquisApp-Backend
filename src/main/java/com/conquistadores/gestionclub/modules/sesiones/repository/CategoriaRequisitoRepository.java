package com.conquistadores.gestionclub.modules.sesiones.repository;

import com.conquistadores.gestionclub.modules.sesiones.model.CategoriaRequisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRequisitoRepository extends JpaRepository<CategoriaRequisito, Long> {
    Optional<CategoriaRequisito> findByNombreIgnoreCase(String nombre);
}
