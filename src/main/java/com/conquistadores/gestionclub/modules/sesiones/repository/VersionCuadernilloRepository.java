package com.conquistadores.gestionclub.modules.sesiones.repository;

import com.conquistadores.gestionclub.modules.sesiones.model.VersionCuadernillo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionCuadernilloRepository extends JpaRepository<VersionCuadernillo, String> {
}
