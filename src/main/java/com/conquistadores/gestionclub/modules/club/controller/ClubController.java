package com.conquistadores.gestionclub.modules.club.controller;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubes")
@CrossOrigin
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Club>> getAllClubes() {
        return ResponseEntity.ok(clubService.getAllClubes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAccessToClub(#id)")
    public ResponseEntity<Club> getClubById(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Club> registrarClub(@RequestBody Club club) {
        return ResponseEntity.ok(clubService.registrarClub(club));
    }

    @PostMapping("/con-director")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Club> registrarClubConDirector(@RequestBody com.conquistadores.gestionclub.modules.club.dto.ClubConDirectorRequest request) {
        return ResponseEntity.ok(clubService.registrarClubConDirector(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Club> actualizarClub(@PathVariable Long id, @RequestBody Club club) {
        return ResponseEntity.ok(clubService.actualizarClub(id, club));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarClub(@PathVariable Long id) {
        clubService.eliminarClub(id);
        return ResponseEntity.noContent().build();
    }
}
