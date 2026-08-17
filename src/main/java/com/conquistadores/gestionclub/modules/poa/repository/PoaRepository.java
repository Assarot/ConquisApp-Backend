package com.conquistadores.gestionclub.modules.poa.repository;

import com.conquistadores.gestionclub.modules.poa.model.Poa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PoaRepository extends JpaRepository<Poa, Long> {
    List<Poa> findByClubIdClub(Long idClub);
    Optional<Poa> findByClubIdClubAndAnio(Long idClub, Integer anio);
    boolean existsByIdPoaAndClubIdClub(Long idPoa, Long idClub);
}
