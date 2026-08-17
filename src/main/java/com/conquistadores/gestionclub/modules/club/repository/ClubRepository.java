package com.conquistadores.gestionclub.modules.club.repository;

import com.conquistadores.gestionclub.modules.club.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
}
