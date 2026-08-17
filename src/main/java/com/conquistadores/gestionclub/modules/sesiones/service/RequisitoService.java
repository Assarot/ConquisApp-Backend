package com.conquistadores.gestionclub.modules.sesiones.service;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.club.repository.ClaseRepository;
import com.conquistadores.gestionclub.modules.especialidades.model.CategoriaEspecialidad;
import com.conquistadores.gestionclub.modules.especialidades.model.Especialidad;
import com.conquistadores.gestionclub.modules.especialidades.repository.CategoriaEspecialidadRepository;
import com.conquistadores.gestionclub.modules.especialidades.repository.EspecialidadRepository;
import com.conquistadores.gestionclub.modules.sesiones.model.CategoriaRequisito;
import com.conquistadores.gestionclub.modules.sesiones.model.Requisito;
import com.conquistadores.gestionclub.modules.sesiones.model.VersionCuadernillo;
import com.conquistadores.gestionclub.modules.sesiones.repository.CategoriaRequisitoRepository;
import com.conquistadores.gestionclub.modules.sesiones.repository.RequisitoRepository;
import com.conquistadores.gestionclub.modules.sesiones.repository.VersionCuadernilloRepository;
import com.conquistadores.gestionclub.modules.sesiones.dto.RequisitoRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequisitoService {

    @Autowired
    private RequisitoRepository requisitoRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private CategoriaRequisitoRepository categoriaRequisitoRepository;

    @Autowired
    private VersionCuadernilloRepository versionCuadernilloRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private CategoriaEspecialidadRepository categoriaEspecialidadRepository;

    public List<Requisito> getRequisitosByClase(Long idClase) {
        return requisitoRepository.findByClaseIdClase(idClase);
    }

    public List<Requisito> getRequisitosByEspecialidad(Long idEspecialidad) {
        return requisitoRepository.findByEspecialidadIdEspecialidad(idEspecialidad);
    }

    @Transactional
    public void importarCuadernillos(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<Requisito> toSave = new ArrayList<>();

            // Find default version or create one
            VersionCuadernillo defaultVersion = versionCuadernilloRepository.findAll().stream()
                    .findFirst()
                    .orElseGet(() -> {
                        VersionCuadernillo v = new VersionCuadernillo();
                        v.setNumeroVersion("v2026");
                        return versionCuadernilloRepository.save(v);
                    });

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header row
                
                String claseName = getCellValueAsString(row.getCell(0));
                String tipoName = getCellValueAsString(row.getCell(1));
                String categoriaName = getCellValueAsString(row.getCell(2));
                String descripcion = getCellValueAsString(row.getCell(3));

                if (claseName.isEmpty() || descripcion.isEmpty()) {
                    continue; // Skip invalid rows
                }

                // 1. Find or create Clase
                Clase clase = claseRepository.findByNombreIgnoreCase(claseName)
                        .orElseGet(() -> {
                            Clase c = new Clase();
                            c.setNombre(claseName);
                            c.setVersionCuadernillo(defaultVersion);
                            return claseRepository.save(c);
                        });

                // 2. Find or create CategoriaRequisito
                if (categoriaName.isEmpty()) categoriaName = "Generalidades";
                final String catName = categoriaName;
                CategoriaRequisito cat = categoriaRequisitoRepository.findByNombreIgnoreCase(catName)
                        .orElseGet(() -> {
                            CategoriaRequisito c = new CategoriaRequisito();
                            c.setNombre(catName);
                            return categoriaRequisitoRepository.save(c);
                        });

                // 3. Map advanced flag
                boolean esAvanzado = "Avanzado".equalsIgnoreCase(tipoName) || "Avanzada".equalsIgnoreCase(tipoName);

                // 4. Create Requisito
                Requisito req = new Requisito();
                req.setClase(clase);
                req.setEsAvanzado(esAvanzado);
                req.setCategoria(cat);
                req.setDescripcion(descripcion);
                req.setVersionCuadernillo(defaultVersion);
                toSave.add(req);
            }
            requisitoRepository.saveAll(toSave);
        }
    }

    @Transactional
    public void importarEspecialidades(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<Requisito> toSave = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header row

                String espName = getCellValueAsString(row.getCell(0));
                String nivelStr = getCellValueAsString(row.getCell(1));
                String anoStr = getCellValueAsString(row.getCell(2));
                String catName = getCellValueAsString(row.getCell(3));
                String reqDesc = getCellValueAsString(row.getCell(4));

                if (espName.isEmpty() || reqDesc.isEmpty()) {
                    continue; // Skip invalid rows
                }

                Integer nivel = 1;
                try {
                    nivel = Integer.parseInt(nivelStr);
                } catch (NumberFormatException ignored) {}

                Integer ano = null;
                try {
                    ano = Integer.parseInt(anoStr);
                } catch (NumberFormatException ignored) {}

                // 1. Find or create CategoriaEspecialidad
                if (catName.isEmpty()) catName = "Habilidades Manuales";
                final String finalCatName = catName;
                CategoriaEspecialidad cat = categoriaEspecialidadRepository.findByNombreIgnoreCase(finalCatName)
                        .orElseGet(() -> {
                            CategoriaEspecialidad c = new CategoriaEspecialidad();
                            c.setNombre(finalCatName);
                            c.setTieneMaestria(true); // default to true
                            return categoriaEspecialidadRepository.save(c);
                        });

                final Integer finalNivel = nivel;
                final Integer finalAno = ano;

                // 2. Find or create Especialidad
                Especialidad esp = especialidadRepository.findByNombreIgnoreCase(espName)
                        .orElseGet(() -> {
                            Especialidad e = new Especialidad();
                            e.setNombre(espName);
                            e.setRequiereExamen(true);
                            e.setPuntosMaestria(10);
                            e.setCategoria(cat);
                            e.setNivelDestreza(finalNivel);
                            e.setAnoIntroduccion(finalAno);
                            e.setDescripcion("Especialidad importada masivamente");
                            e.setImagenUrl("default_specialty");
                            return especialidadRepository.save(e);
                        });

                // Update fields if they were modified/provided in row
                boolean modified = false;
                if (esp.getCategoria() == null || !esp.getCategoria().getIdCategoriaEspecialidad().equals(cat.getIdCategoriaEspecialidad())) {
                    esp.setCategoria(cat);
                    modified = true;
                }
                if (nivel != null && !nivel.equals(esp.getNivelDestreza())) {
                    esp.setNivelDestreza(nivel);
                    modified = true;
                }
                if (ano != null && !ano.equals(esp.getAnoIntroduccion())) {
                    esp.setAnoIntroduccion(ano);
                    modified = true;
                }
                if (modified) {
                    esp = especialidadRepository.save(esp);
                }

                // 3. Create Requisito
                Requisito req = new Requisito();
                req.setEspecialidad(esp);
                req.setDescripcion(reqDesc);
                req.setEsAvanzado(false);
                toSave.add(req);
            }
            requisitoRepository.saveAll(toSave);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    @Transactional
    public Requisito registrarRequisito(RequisitoRequest request) {
        Requisito req = new Requisito();
        req.setDescripcion(request.getDescripcion());
        req.setEsAvanzado(request.getEsAvanzado() != null ? request.getEsAvanzado() : false);
        
        if (request.getIdClase() != null) {
            Clase clase = claseRepository.findById(request.getIdClase())
                    .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
            req.setClase(clase);
            req.setVersionCuadernillo(clase.getVersionCuadernillo());
        }
        
        if (request.getIdEspecialidad() != null) {
            Especialidad esp = especialidadRepository.findById(request.getIdEspecialidad())
                    .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
            req.setEspecialidad(esp);
        }
        
        if (request.getIdCategoria() != null) {
            CategoriaRequisito cat = categoriaRequisitoRepository.findById(request.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría de requisito no encontrada"));
            req.setCategoria(cat);
        } else {
            // Default category "Generalidades" if it's class requirement
            if (request.getIdClase() != null) {
                CategoriaRequisito defaultCat = categoriaRequisitoRepository.findByNombreIgnoreCase("Generalidades")
                        .orElseGet(() -> {
                            CategoriaRequisito c = new CategoriaRequisito();
                            c.setNombre("Generalidades");
                            return categoriaRequisitoRepository.save(c);
                        });
                req.setCategoria(defaultCat);
            }
        }
        
        return requisitoRepository.save(req);
    }

    @Transactional
    public void eliminarRequisito(Long id) {
        requisitoRepository.deleteById(id);
    }
}
