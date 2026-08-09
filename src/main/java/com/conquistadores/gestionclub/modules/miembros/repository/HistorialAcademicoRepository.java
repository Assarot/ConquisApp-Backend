package com.conquistadores.gestionclub.modules.miembros.repository;

import com.conquistadores.gestionclub.modules.miembros.model.HistorialAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialAcademicoRepository extends JpaRepository<HistorialAcademico, String> {
    List<HistorialAcademico> findByMiembroIdMiembro(String idMiembro);
}
