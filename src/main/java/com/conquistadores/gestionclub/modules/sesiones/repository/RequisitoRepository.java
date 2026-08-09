package com.conquistadores.gestionclub.modules.sesiones.repository;

import com.conquistadores.gestionclub.modules.sesiones.model.Requisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequisitoRepository extends JpaRepository<Requisito, String> {
    List<Requisito> findByVersionCuadernilloIdVersionCuadernillo(String idVersionCuadernillo);
}
