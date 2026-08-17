package com.conquistadores.gestionclub.modules.sesiones.controller;

import com.conquistadores.gestionclub.modules.sesiones.model.Requisito;
import com.conquistadores.gestionclub.modules.sesiones.service.RequisitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requisitos")
@CrossOrigin
public class RequisitoController {

    @Autowired
    private RequisitoService requisitoService;

    @GetMapping("/clase/{idClase}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Requisito>> getRequisitosByClase(@PathVariable Long idClase) {
        return ResponseEntity.ok(requisitoService.getRequisitosByClase(idClase));
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Requisito>> getRequisitosByEspecialidad(@PathVariable Long idEspecialidad) {
        return ResponseEntity.ok(requisitoService.getRequisitosByEspecialidad(idEspecialidad));
    }

    @PostMapping("/importar-cuadernillos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> importarCuadernillos(@RequestParam("file") MultipartFile file) {
        try {
            requisitoService.importarCuadernillos(file);
            return ResponseEntity.ok("Requisitos del cuadernillo importados exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al importar cuadernillos: " + e.getMessage());
        }
    }

    @PostMapping("/importar-especialidades")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> importarEspecialidades(@RequestParam("file") MultipartFile file) {
        try {
            requisitoService.importarEspecialidades(file);
            return ResponseEntity.ok("Especialidades y sus requisitos importados exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al importar especialidades: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Requisito> registrarRequisito(@RequestBody com.conquistadores.gestionclub.modules.sesiones.dto.RequisitoRequest request) {
        return ResponseEntity.ok(requisitoService.registrarRequisito(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarRequisito(@PathVariable Long id) {
        requisitoService.eliminarRequisito(id);
        return ResponseEntity.noContent().build();
    }
}
