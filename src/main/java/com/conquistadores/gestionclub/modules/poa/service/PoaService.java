package com.conquistadores.gestionclub.modules.poa.service;

import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.modules.poa.event.PoaUpdatedEvent;
import com.conquistadores.gestionclub.modules.poa.model.ActividadPoa;
import com.conquistadores.gestionclub.modules.poa.model.Poa;
import com.conquistadores.gestionclub.modules.poa.repository.ActividadPoaRepository;
import com.conquistadores.gestionclub.modules.poa.repository.PoaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PoaService {

    @Autowired
    private PoaRepository poaRepository;

    @Autowired
    private ActividadPoaRepository actividadPoaRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<Poa> getPoasByClub(Long idClub) {
        return poaRepository.findByClubIdClub(idClub);
    }

    public List<ActividadPoa> getActividadesByPoa(Long idPoa) {
        return actividadPoaRepository.findByPoaIdPoaOrderByFechaAsc(idPoa);
    }

    @Transactional
    public Poa crearPoa(Long idClub, Integer anio) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));

        poaRepository.findByClubIdClubAndAnio(idClub, anio).ifPresent(p -> {
            throw new RuntimeException("El POA de este año ya existe para este club.");
        });

        Poa poa = new Poa();
        poa.setClub(club);
        poa.setAnio(anio);
        poa.setEstado("BORRADOR");
        return poaRepository.save(poa);
    }

    @Transactional
    public ActividadPoa registrarActividad(Long idPoa, ActividadPoa nuevaActividad) {
        Poa poa = poaRepository.findById(idPoa)
                .orElseThrow(() -> new RuntimeException("POA no encontrado"));

        nuevaActividad.setPoa(poa);
        ActividadPoa saved = actividadPoaRepository.save(nuevaActividad);
        
        // Disparar evento para sincronizar cronogramas
        eventPublisher.publishEvent(new PoaUpdatedEvent(this, saved.getIdActividad(), "CREATE"));
        
        return saved;
    }

    @Transactional
    public ActividadPoa actualizarFechaActividad(Long idActividad, LocalDate nuevaFecha) {
        ActividadPoa actividad = actividadPoaRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

        actividad.setFecha(nuevaFecha);
        ActividadPoa updated = actividadPoaRepository.save(actividad);

        // Disparar evento para sincronizar cronogramas automáticamente (RN-03)
        eventPublisher.publishEvent(new PoaUpdatedEvent(this, updated.getIdActividad(), "UPDATE_DATE"));

        return updated;
    }

    /**
     * Lee el valor de una celda como String de forma robusta,
     * manejando tipos NUMERIC, STRING, BOOLEAN y FORMULA.
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Devolver la fecha como texto ISO para parseo posterior
                    LocalDate date = cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    return date.toString();
                }
                // Evitar decimales innecesarios (ej. 1.0 → "1")
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num) && !Double.isInfinite(num)) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            default:
                return "";
        }
    }


    @Transactional
    public List<ActividadPoa> importarDesdeExcel(Long idPoa, MultipartFile file) throws IOException {
        Poa poa = poaRepository.findById(idPoa)
                .orElseThrow(() -> new RuntimeException("POA no encontrado"));

        List<ActividadPoa> importadas = new ArrayList<>();
        List<ActividadPoa> actividadesExistentes = actividadPoaRepository.findByPoaIdPoaOrderByFechaAsc(idPoa);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // ── Detectar columnas dinámicamente desde la cabecera ──────────────
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("El archivo Excel no tiene fila de cabecera.");
            }

            // Mapear nombre de columna normalizado → índice
            Map<String, Integer> colMap = new HashMap<>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                Cell cell = headerRow.getCell(c);
                if (cell == null) continue;
                String header = getCellStringValue(cell)
                        .toLowerCase()
                        .replaceAll("[áà]", "a")
                        .replaceAll("[éè]", "e")
                        .replaceAll("[íì]", "i")
                        .replaceAll("[óò]", "o")
                        .replaceAll("[úù]", "u")
                        .trim();
                colMap.put(header, c);
            }

            // Resolver índices (con alias comunes)
            Integer colFecha = colMap.containsKey("fecha inicio") ? colMap.get("fecha inicio") : colMap.get("fecha");
            Integer colFechaFin = colMap.containsKey("fecha fin") ? colMap.get("fecha fin") : colMap.get("fechafin");
            Integer colActividad = colMap.containsKey("actividad") ? colMap.get("actividad") : colMap.get("nombre");
            Integer colLugar = colMap.get("lugar");
            Integer colAmbito = colMap.get("ambito");
            Integer colResponsable = colMap.get("responsable");

            // Validar que al menos Fecha y Actividad existan
            if (colFecha == null || colActividad == null) {
                throw new RuntimeException(
                    "No se encontraron las columnas obligatorias 'Fecha' o 'Fecha Inicio' y 'Actividad' en la cabecera del Excel. " +
                    "Columnas detectadas: " + colMap.keySet());
            }

            // ── Leer filas de datos ────────────────────────────────────────────
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Fecha Inicio (obligatoria)
                Cell fechaCell = row.getCell(colFecha);
                if (fechaCell == null) continue;
                LocalDate fecha;
                if (fechaCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(fechaCell)) {
                    fecha = fechaCell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    String fechaStr = getCellStringValue(fechaCell);
                    if (fechaStr.isEmpty()) continue;
                    fecha = LocalDate.parse(fechaStr);
                }

                // Fecha Fin (opcional)
                LocalDate fechaFin = null;
                if (colFechaFin != null) {
                    Cell fechaFinCell = row.getCell(colFechaFin);
                    if (fechaFinCell != null) {
                        if (fechaFinCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(fechaFinCell)) {
                            fechaFin = fechaFinCell.getDateCellValue().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate();
                        } else {
                            String fechaFinStr = getCellStringValue(fechaFinCell);
                            if (!fechaFinStr.isEmpty()) {
                                fechaFin = LocalDate.parse(fechaFinStr);
                            }
                        }
                    }
                }

                // Actividad / Nombre (obligatorio)
                String nombre = colActividad != null ? getCellStringValue(row.getCell(colActividad)) : "";
                if (nombre.isEmpty()) continue;

                // Lugar (opcional)
                String lugar = colLugar != null ? getCellStringValue(row.getCell(colLugar)) : "";

                // Ámbito (opcional, default CLUB)
                String ambito = colAmbito != null ? getCellStringValue(row.getCell(colAmbito)).toUpperCase() : "CLUB";
                if (ambito.isEmpty()) ambito = "CLUB";

                // Responsable (opcional — se importa tal cual, incluso valores personalizados)
                String responsable = colResponsable != null ? getCellStringValue(row.getCell(colResponsable)) : "";

                final LocalDate finalFecha = fecha;
                final String finalNombre = nombre.trim();
                Optional<ActividadPoa> actividadExistente = actividadesExistentes.stream()
                        .filter(a -> a.getNombre().equalsIgnoreCase(finalNombre) && a.getFecha().equals(finalFecha))
                        .findFirst();

                ActividadPoa actividad;
                String eventAction;
                if (actividadExistente.isPresent()) {
                    actividad = actividadExistente.get();
                    eventAction = "UPDATE_DATE";
                } else {
                    actividad = new ActividadPoa();
                    actividad.setPoa(poa);
                    actividad.setFecha(fecha);
                    eventAction = "CREATE";
                }

                actividad.setFechaFin(fechaFin);
                actividad.setResponsable(responsable.isEmpty() ? "Sin asignar" : responsable);
                actividad.setNombre(nombre);
                actividad.setLugar(lugar);
                actividad.setAmbito(ambito);

                ActividadPoa saved = actividadPoaRepository.save(actividad);
                eventPublisher.publishEvent(new PoaUpdatedEvent(this, saved.getIdActividad(), eventAction));
                importadas.add(saved);
            }
        }

        return importadas;
    }

    /**
     * Actualiza todos los campos editables de una actividad.
     */
    @Transactional
    public ActividadPoa actualizarActividad(Long idActividad, ActividadPoa datos) {
        ActividadPoa actividad = actividadPoaRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

        if (datos.getNombre()  != null) actividad.setNombre(datos.getNombre());
        if (datos.getFecha()   != null) actividad.setFecha(datos.getFecha());
        actividad.setFechaFin(datos.getFechaFin());
        if (datos.getAmbito()  != null) actividad.setAmbito(datos.getAmbito());
        if (datos.getLugar()   != null) actividad.setLugar(datos.getLugar());
        if (datos.getResponsable() != null) actividad.setResponsable(datos.getResponsable());

        ActividadPoa updated = actividadPoaRepository.save(actividad);
        eventPublisher.publishEvent(new PoaUpdatedEvent(this, updated.getIdActividad(), "UPDATE_DATE"));
        return updated;
    }

    /**
     * Elimina una actividad del POA y publica un evento DELETE
     * para que el cronograma quede sincronizado.
     */
    @Transactional
    public void eliminarActividad(Long idActividad) {
        ActividadPoa actividad = actividadPoaRepository.findById(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));

        actividadPoaRepository.delete(actividad);
        eventPublisher.publishEvent(new PoaUpdatedEvent(this, idActividad, "DELETE"));
    }

    /**
     * Genera un archivo Excel (.xlsx) con todas las actividades del POA indicado.
     * Columnas: Fecha | Actividad | Lugar | Ambito | Responsable
     * La columna Responsable incluye una lista desplegable con los valores estAndar.
     */
    public byte[] exportarExcel(Long idPoa) throws IOException {
        List<ActividadPoa> actividades = actividadPoaRepository.findByPoaIdPoaOrderByFechaAsc(idPoa);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("POA");

            // --- Estilo cabecera ---
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            // Nuevo orden: Fecha Inicio | Fecha Fin | Actividad | Lugar | Ambito | Responsable
            // Columna:      A              B           C           D       E        F
            String[] columns = {"Fecha Inicio", "Fecha Fin", "Actividad", "Lugar", "Ambito", "Responsable"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Estilo de fecha ---
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));

            // --- Filas de datos ---
            int rowIdx = 1;
            for (ActividadPoa act : actividades) {
                Row row = sheet.createRow(rowIdx++);

                // Col A: Fecha Inicio
                Cell fechaInicioCell = row.createCell(0);
                if (act.getFecha() != null) {
                    fechaInicioCell.setCellValue(
                        java.util.Date.from(act.getFecha().atStartOfDay(ZoneId.systemDefault()).toInstant())
                    );
                    fechaInicioCell.setCellStyle(dateStyle);
                }

                // Col B: Fecha Fin
                Cell fechaFinCell = row.createCell(1);
                LocalDate end = act.getFechaFin() != null ? act.getFechaFin() : act.getFecha();
                if (end != null) {
                    fechaFinCell.setCellValue(
                        java.util.Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    );
                    fechaFinCell.setCellStyle(dateStyle);
                }

                // Col C: Actividad
                row.createCell(2).setCellValue(act.getNombre() != null ? act.getNombre() : "");
                // Col D: Lugar
                row.createCell(3).setCellValue(act.getLugar() != null ? act.getLugar() : "");
                // Col E: Ambito
                row.createCell(4).setCellValue(act.getAmbito() != null ? act.getAmbito() : "");
                // Col F: Responsable
                row.createCell(5).setCellValue(act.getResponsable() != null ? act.getResponsable() : "");
            }

            // --- Dropdown (Data Validation) en columna F (Responsable) ---
            // Responsables estándar disponibles en el desplegable
            String[] responsablesEstandar = {
                "Jóvenes", "Directiva", "Director",
                "Director Asociado", "Secretario", "Tesorero"
            };
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(responsablesEstandar);
            // Aplica a las filas de datos (fila 2 en adelante, columna F=índice 5)
            org.apache.poi.ss.util.CellRangeAddressList addressList =
                    new org.apache.poi.ss.util.CellRangeAddressList(1, 1000, 5, 5);
            DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
            validation.setSuppressDropDownArrow(false); // Mostrar flecha del dropdown
            validation.setShowErrorBox(false);           // Permitir valores personalizados
            validation.setShowPromptBox(true);
            validation.createPromptBox("Responsable(s)",
                    "Usa la lista ▼ para elegir un responsable.\n" +
                    "Puedes escribir varios separados por coma.\n" +
                    "Ej: Directiva, Jóvenes, Director\n" +
                    "También puedes agregar nombres nuevos.");
            sheet.addValidationData(validation);

            // --- Dropdown en columna E (Ambito) ---
            String[] ambitos = {"CLUB", "IGLESIA", "REGION", "ASOCIACION", "RECURRENTE"};
            DataValidationConstraint ambitoConstraint = dvHelper.createExplicitListConstraint(ambitos);
            org.apache.poi.ss.util.CellRangeAddressList ambitoRange =
                    new org.apache.poi.ss.util.CellRangeAddressList(1, 1000, 4, 4);
            DataValidation ambitoValidation = dvHelper.createValidation(ambitoConstraint, ambitoRange);
            ambitoValidation.setSuppressDropDownArrow(false);
            ambitoValidation.setShowErrorBox(true);
            sheet.addValidationData(ambitoValidation);

            // --- Auto-ajustar ancho de columnas ---
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // --- Anchos mínimos para que el Excel se vea limpio desde el inicio ---
            // 256 unidades = 1 carácter de ancho en Excel
            int[] minWidths = {
                14 * 256,   // A: Fecha Inicio   → mínimo 14 chars
                14 * 256,   // B: Fecha Fin      → mínimo 14 chars
                30 * 256,   // C: Actividad      → mínimo 30 chars
                18 * 256,   // D: Lugar          → mínimo 18 chars
                14 * 256,   // E: Ambito         → mínimo 14 chars
                28 * 256    // F: Responsable    → mínimo 28 chars
            };
            for (int i = 0; i < columns.length; i++) {
                if (sheet.getColumnWidth(i) < minWidths[i]) {
                    sheet.setColumnWidth(i, minWidths[i]);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
