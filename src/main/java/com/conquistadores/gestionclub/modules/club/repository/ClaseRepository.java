package com.conquistadores.gestionclub.modules.club.repository;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByClubIdClub(Long idClub);
    Optional<Clase> findByNombreIgnoreCase(String nombre);
}
