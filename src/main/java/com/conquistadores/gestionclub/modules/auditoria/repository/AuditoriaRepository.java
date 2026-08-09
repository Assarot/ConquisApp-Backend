package com.conquistadores.gestionclub.modules.auditoria.repository;

import com.conquistadores.gestionclub.modules.auditoria.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, String> {
    List<Auditoria> findByModulo(String modulo);
}
