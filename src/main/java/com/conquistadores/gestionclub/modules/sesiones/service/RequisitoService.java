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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

                // 4. Create Requisito if it does not already exist
                final String finalDesc = descripcion;
                boolean alreadyExists = requisitoRepository.findByClaseIdClase(clase.getIdClase())
                        .stream()
                        .anyMatch(r -> r.getDescripcion().equalsIgnoreCase(finalDesc) && r.getEsAvanzado().equals(esAvanzado));

                if (!alreadyExists) {
                    boolean addedInSession = toSave.stream()
                            .anyMatch(r -> r.getClase() != null && r.getClase().getIdClase().equals(clase.getIdClase())
                                    && r.getDescripcion().equalsIgnoreCase(finalDesc) && r.getEsAvanzado().equals(esAvanzado));
                    if (!addedInSession) {
                        Requisito req = new Requisito();
                        req.setClase(clase);
                        req.setEsAvanzado(esAvanzado);
                        req.setCategoria(cat);
                        req.setDescripcion(descripcion);
                        req.setVersionCuadernillo(defaultVersion);
                        toSave.add(req);
                    }
                }
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

                // 3. Create Requisito if it does not already exist
                final String finalReqDesc = reqDesc;
                final Especialidad finalEsp = esp;
                boolean alreadyExists = requisitoRepository.findByEspecialidadIdEspecialidad(esp.getIdEspecialidad())
                        .stream()
                        .anyMatch(r -> r.getDescripcion().equalsIgnoreCase(finalReqDesc));

                if (!alreadyExists) {
                    boolean addedInSession = toSave.stream()
                            .anyMatch(r -> r.getEspecialidad() != null && r.getEspecialidad().getIdEspecialidad().equals(finalEsp.getIdEspecialidad())
                                    && r.getDescripcion().equalsIgnoreCase(finalReqDesc));
                    if (!addedInSession) {
                        Requisito req = new Requisito();
                        req.setEspecialidad(esp);
                        req.setDescripcion(reqDesc);
                        req.setEsAvanzado(false);
                        toSave.add(req);
                    }
                }
            }
            requisitoRepository.saveAll(toSave);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportarEspecialidades() throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Especialidades");
            
            // Create Header Row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nombre Especialidad");
            header.createCell(1).setCellValue("Nivel Destreza");
            header.createCell(2).setCellValue("Año Introducción");
            header.createCell(3).setCellValue("Categoría");
            header.createCell(4).setCellValue("Descripción del Requisito");
            
            // Fetch all requirements that belong to a specialty
            List<Requisito> reqs = requisitoRepository.findAll().stream()
                    .filter(r -> r.getEspecialidad() != null)
                    .collect(Collectors.toList());
            
            int rowIdx = 1;
            for (Requisito r : reqs) {
                Row row = sheet.createRow(rowIdx++);
                Especialidad esp = r.getEspecialidad();
                row.createCell(0).setCellValue(esp.getNombre());
                row.createCell(1).setCellValue(esp.getNivelDestreza() != null ? esp.getNivelDestreza().toString() : "1");
                row.createCell(2).setCellValue(esp.getAnoIntroduccion() != null ? esp.getAnoIntroduccion().toString() : "");
                row.createCell(3).setCellValue(esp.getCategoria() != null ? esp.getCategoria().getNombre() : "");
                row.createCell(4).setCellValue(r.getDescripcion());
            }
            
            // Also fetch specialties that have NO requirements yet so they are not left out
            List<Especialidad> allEsps = especialidadRepository.findAll();
            for (Especialidad esp : allEsps) {
                final Especialidad finalEsp = esp;
                boolean hasReq = reqs.stream().anyMatch(r -> r.getEspecialidad().getIdEspecialidad().equals(finalEsp.getIdEspecialidad()));
                if (!hasReq) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(esp.getNombre());
                    row.createCell(1).setCellValue(esp.getNivelDestreza() != null ? esp.getNivelDestreza().toString() : "1");
                    row.createCell(2).setCellValue(esp.getAnoIntroduccion() != null ? esp.getAnoIntroduccion().toString() : "");
                    row.createCell(3).setCellValue(esp.getCategoria() != null ? esp.getCategoria().getNombre() : "");
                    row.createCell(4).setCellValue(""); // empty requirement
                }
            }
            
            // Auto size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportarCuadernillos() throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Cuadernillos");
            
            // Create Header Row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Clase");
            header.createCell(1).setCellValue("Tipo");
            header.createCell(2).setCellValue("Categoría");
            header.createCell(3).setCellValue("Descripción del Requisito");
            
            // Fetch all requirements that belong to a class
            List<Requisito> reqs = requisitoRepository.findAll().stream()
                    .filter(r -> r.getClase() != null)
                    .collect(Collectors.toList());
            
            int rowIdx = 1;
            for (Requisito r : reqs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getClase().getNombre());
                row.createCell(1).setCellValue(r.getEsAvanzado() ? "Avanzado" : "Regular");
                row.createCell(2).setCellValue(r.getCategoria() != null ? r.getCategoria().getNombre() : "I. Generales");
                row.createCell(3).setCellValue(r.getDescripcion());
            }
            
            // Auto size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
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

    public List<CategoriaRequisito> getCategorias() {
        return categoriaRequisitoRepository.findAll();
    }

    @Transactional
    public CategoriaRequisito crearCategoria(CategoriaRequisito categoria) {
        return categoriaRequisitoRepository.save(categoria);
    }

    @Transactional
    public CategoriaRequisito actualizarCategoria(Long id, CategoriaRequisito details) {
        CategoriaRequisito existing = categoriaRequisitoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        existing.setNombre(details.getNombre());
        return categoriaRequisitoRepository.save(existing);
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        categoriaRequisitoRepository.deleteById(id);
    }

    @Transactional
    public void eliminarRequisito(Long id) {
        requisitoRepository.deleteById(id);
    }
}
