package com.conquistadores.gestionclub.modules.poa.repository;

import com.conquistadores.gestionclub.modules.poa.model.ActividadPoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActividadPoaRepository extends JpaRepository<ActividadPoa, Long> {
    List<ActividadPoa> findByPoaIdPoa(Long idPoa);
}
