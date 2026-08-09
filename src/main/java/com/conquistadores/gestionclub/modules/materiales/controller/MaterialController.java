package com.conquistadores.gestionclub.modules.materiales.controller;

import com.conquistadores.gestionclub.modules.materiales.model.Material;
import com.conquistadores.gestionclub.modules.materiales.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materiales")
@CrossOrigin
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/clase/{idClase}")
    public ResponseEntity<List<Material>> getMaterialesByClase(@PathVariable String idClase) {
        return ResponseEntity.ok(materialService.getMaterialesByClase(idClase));
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    public ResponseEntity<List<Material>> getMaterialesByEspecialidad(@PathVariable String idEspecialidad) {
        return ResponseEntity.ok(materialService.getMaterialesByEspecialidad(idEspecialidad));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO', 'INSTRUCTOR')")
    public ResponseEntity<Material> guardarMaterial(@RequestBody Material material) {
        return ResponseEntity.ok(materialService.guardarMaterial(material));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO')")
    public ResponseEntity<Void> eliminarMaterial(@PathVariable String id) {
        materialService.eliminarMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
