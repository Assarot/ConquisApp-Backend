package com.conquistadores.gestionclub.modules.miembros.repository;

import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Long> {
    List<Miembro> findByClubIdClub(Long idClub);
    List<Miembro> findByUnidadIdUnidad(Long idUnidad);
    List<Miembro> findByClaseIdClase(Long idClase);
}
