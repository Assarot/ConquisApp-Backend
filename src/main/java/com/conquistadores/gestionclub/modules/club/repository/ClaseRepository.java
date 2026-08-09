package com.conquistadores.gestionclub.modules.club.repository;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, String> {
    List<Clase> findByClubIdClub(String idClub);
}
