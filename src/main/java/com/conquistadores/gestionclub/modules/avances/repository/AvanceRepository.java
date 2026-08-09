package com.conquistadores.gestionclub.modules.avances.repository;

import com.conquistadores.gestionclub.modules.avances.model.Avance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvanceRepository extends JpaRepository<Avance, String> {
    List<Avance> findByMiembroIdMiembro(String idMiembro);
    Optional<Avance> findByMiembroIdMiembroAndRequisitoIdRequisito(String idMiembro, String idRequisito);
}
