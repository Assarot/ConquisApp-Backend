package com.conquistadores.gestionclub.modules.poa.repository;

import com.conquistadores.gestionclub.modules.poa.model.BloqueCronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BloqueCronogramaRepository extends JpaRepository<BloqueCronograma, String> {
    List<BloqueCronograma> findByCronogramaIdCronograma(String idCronograma);
}
