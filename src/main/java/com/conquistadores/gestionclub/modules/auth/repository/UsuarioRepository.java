package com.conquistadores.gestionclub.modules.auth.repository;

import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByClubIdClub(Long idClub);
}
