package com.conquistadores.gestionclub.modules.materiales.service;

import com.conquistadores.gestionclub.modules.materiales.model.Material;
import com.conquistadores.gestionclub.modules.materiales.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> getMaterialesByClase(String idClase) {
        return materialRepository.findByClaseIdClase(idClase);
    }

    public List<Material> getMaterialesByEspecialidad(String idEspecialidad) {
        return materialRepository.findByEspecialidadIdEspecialidad(idEspecialidad);
    }

    public Material guardarMaterial(Material material) {
        return materialRepository.save(material);
    }

    public void eliminarMaterial(String idMaterial) {
        materialRepository.deleteById(idMaterial);
    }
}
