package com.conquistadores.gestionclub.modules.miembros.repository;

import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, String> {
    List<Miembro> findByClubIdClub(String idClub);
    List<Miembro> findByUnidadIdUnidad(String idUnidad);
    List<Miembro> findByClaseIdClase(String idClase);
}
