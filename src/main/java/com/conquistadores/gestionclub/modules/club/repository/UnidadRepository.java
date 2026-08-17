package com.conquistadores.gestionclub.modules.club.repository;

import com.conquistadores.gestionclub.modules.club.model.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {
    List<Unidad> findByClubIdClub(Long idClub);
}
