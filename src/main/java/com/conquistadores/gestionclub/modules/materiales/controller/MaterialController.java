package com.conquistadores.gestionclub.modules.materiales.controller;

import com.conquistadores.gestionclub.modules.materiales.model.Material;
import com.conquistadores.gestionclub.modules.materiales.service.MaterialService;
import com.conquistadores.gestionclub.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materiales")
@CrossOrigin
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/clase/{idClase}")
    @PreAuthorize("@securityService.hasAccessToClase(#idClase)")
    public ResponseEntity<List<Material>> getMaterialesByClase(@PathVariable Long idClase) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long idClub = userDetails.getUsuario().getClub() != null ? userDetails.getUsuario().getClub().getIdClub() : null;
        return ResponseEntity.ok(materialService.getMaterialesByClase(idClase, idClub));
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    @PreAuthorize("@securityService.hasAccessToEspecialidad(#idEspecialidad)")
    public ResponseEntity<List<Material>> getMaterialesByEspecialidad(@PathVariable Long idEspecialidad) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long idClub = userDetails.getUsuario().getClub() != null ? userDetails.getUsuario().getClub().getIdClub() : null;
        return ResponseEntity.ok(materialService.getMaterialesByEspecialidad(idEspecialidad, idClub));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<Material> guardarMaterial(@RequestBody Material material) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        material.setUsuarioCreador(userDetails.getUsuario());
        return ResponseEntity.ok(materialService.guardarMaterial(material));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') and @securityService.hasAccessToMaterial(#id)")
    public ResponseEntity<Void> eliminarMaterial(@PathVariable Long id) {
        materialService.eliminarMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
