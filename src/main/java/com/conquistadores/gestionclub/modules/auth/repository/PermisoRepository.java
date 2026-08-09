package com.conquistadores.gestionclub.modules.auth.repository;

import com.conquistadores.gestionclub.modules.auth.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, String> {
    List<Permiso> findByRolIdRol(String idRol);
}
