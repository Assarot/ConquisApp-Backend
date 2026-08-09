package com.conquistadores.gestionclub.modules.auth.repository;

import com.conquistadores.gestionclub.modules.auth.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, String> {
    Optional<Rol> findByNombre(String nombre);
}
