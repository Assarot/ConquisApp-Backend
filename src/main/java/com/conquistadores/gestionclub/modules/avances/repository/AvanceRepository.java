package com.conquistadores.gestionclub.modules.avances.repository;

import com.conquistadores.gestionclub.modules.avances.model.Avance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvanceRepository extends JpaRepository<Avance, Long> {
    List<Avance> findByMiembroIdMiembro(Long idMiembro);
    Optional<Avance> findByMiembroIdMiembroAndRequisitoIdRequisito(Long idMiembro, Long idRequisito);
}
