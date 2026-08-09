package com.conquistadores.gestionclub.modules.rankings.repository;

import com.conquistadores.gestionclub.modules.rankings.model.RankingUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RankingUnidadRepository extends JpaRepository<RankingUnidad, String> {
    List<RankingUnidad> findByUnidadIdUnidad(String idUnidad);
}
