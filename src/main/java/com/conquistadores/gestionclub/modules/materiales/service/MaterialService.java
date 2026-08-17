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

    public List<Material> getMaterialesByClase(Long idClase, Long idClub) {
        if (idClub == null) {
            return materialRepository.findByClaseIdClase(idClase);
        }
        return materialRepository.findByClaseIdClaseAndUsuarioCreadorClubIdClub(idClase, idClub);
    }

    public List<Material> getMaterialesByEspecialidad(Long idEspecialidad, Long idClub) {
        if (idClub == null) {
            return materialRepository.findByEspecialidadIdEspecialidad(idEspecialidad);
        }
        return materialRepository.findByEspecialidadIdEspecialidadAndUsuarioCreadorClubIdClub(idEspecialidad, idClub);
    }

    public Material guardarMaterial(Material material) {
        return materialRepository.save(material);
    }

    public void eliminarMaterial(Long idMaterial) {
        materialRepository.deleteById(idMaterial);
    }
}
