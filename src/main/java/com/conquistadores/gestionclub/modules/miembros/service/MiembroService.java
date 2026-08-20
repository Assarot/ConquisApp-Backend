package com.conquistadores.gestionclub.modules.miembros.service;

import com.conquistadores.gestionclub.modules.club.model.Clase;
import com.conquistadores.gestionclub.modules.club.model.Club;
import com.conquistadores.gestionclub.modules.club.model.Unidad;
import com.conquistadores.gestionclub.modules.club.repository.ClaseRepository;
import com.conquistadores.gestionclub.modules.club.repository.ClubRepository;
import com.conquistadores.gestionclub.modules.club.repository.UnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.model.HistorialUnidad;
import com.conquistadores.gestionclub.modules.miembros.model.Miembro;
import com.conquistadores.gestionclub.modules.miembros.repository.HistorialUnidadRepository;
import com.conquistadores.gestionclub.modules.miembros.repository.MiembroRepository;
import com.conquistadores.gestionclub.modules.auth.model.Usuario;
import com.conquistadores.gestionclub.modules.auth.repository.UsuarioRepository;
import com.conquistadores.gestionclub.modules.notificaciones.service.NotificacionService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MiembroService {

    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private HistorialUnidadRepository historialUnidadRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Miembro> getMiembrosByClub(Long idClub) {
        return miembroRepository.findByClubIdClub(idClub);
    }

    public Optional<Miembro> getMiembroById(Long idMiembro) {
        return miembroRepository.findById(idMiembro);
    }

    @Transactional
    public Miembro registrarMiembro(Miembro miembro) {
        calcularPendientes(miembro);
        Miembro saved = miembroRepository.save(miembro);
        autoAssignUnidadConsejero(saved);
        return saved;
    }

    private void autoAssignUnidadConsejero(Miembro miembro) {
        if (miembro.getFuncion() != null && miembro.getFuncion().equalsIgnoreCase("CONSEJERO") 
                && miembro.getUnidad() != null && miembro.getClub() != null) {
            
            List<Usuario> matchingUsers = usuarioRepository.findByClubIdClub(miembro.getClub().getIdClub());
            for (Usuario u : matchingUsers) {
                if (u.getNombre().equalsIgnoreCase(miembro.getNombre()) 
                        && u.getApellido().equalsIgnoreCase(miembro.getApellido())) {
                    
                    Unidad unidad = miembro.getUnidad();
                    unidad.setConsejero(u);
                    unidadRepository.save(unidad);
                    break;
                }
            }
        }
    }

    @Transactional
    public Miembro cambiarUnidad(Long idMiembro, Long idUnidadDestino) {
        Miembro miembro = miembroRepository.findById(idMiembro)
                .orElseThrow(() -> new RuntimeException("Miembro no encontrado"));

        Unidad unidadDestino = unidadRepository.findById(idUnidadDestino)
                .orElseThrow(() -> new RuntimeException("Unidad destino no encontrada"));

        Unidad unidadOrigen = miembro.getUnidad();
        
        // Actualizar unidad del miembro
        miembro.setUnidad(unidadDestino);
        Miembro updated = miembroRepository.save(miembro);

        // Guardar registro en historial (RN-22)
        HistorialUnidad historial = new HistorialUnidad();
        historial.setMiembro(updated);
        historial.setUnidadOrigen(unidadOrigen);
        historial.setUnidadDestino(unidadDestino);
        historial.setFechaCambio(LocalDateTime.now());
        historialUnidadRepository.save(historial);

        // Trigger notification to the new Unit's Counselor if exists
        if (unidadDestino.getConsejero() != null) {
            notificacionService.registrarNotificacion(
                unidadDestino.getConsejero().getIdUsuario(),
                "El miembro " + miembro.getNombre() + " " + miembro.getApellido() + " ha sido asignado a tu unidad: " + unidadDestino.getNombre() + "."
            );
        }

        return updated;
    }

    @Transactional
    public Miembro inactivarMiembro(Long idMiembro) {
        Miembro miembro = miembroRepository.findById(idMiembro)
                .orElseThrow(() -> new RuntimeException("Miembro no encontrado"));

        // Los miembros nunca se eliminan físicamente del sistema (RN-20, RN-24)
        miembro.setEstado("INACTIVO");
        return miembroRepository.save(miembro);
    }

    @Transactional
    public void importarMiembrosExcel(MultipartFile file, Long idClub) {
        Club club = clubRepository.findById(idClub)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));

        // Cargar primera unidad y clase por defecto para nuevos miembros
        List<Unidad> unidades = unidadRepository.findByClubIdClub(idClub);
        List<Clase> clases = claseRepository.findAll();

        if (unidades.isEmpty() || clases.isEmpty()) {
            throw new RuntimeException("El club debe tener al menos una unidad y una clase antes de importar miembros.");
        }

        Unidad unidadDefecto = unidades.get(0);
        Clase claseDefecto = clases.get(0);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("El archivo Excel no tiene fila de cabecera.");
            }

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

            Integer colNombre = colMap.get("nombre");
            Integer colApellido = colMap.get("apellido");
            Integer colFuncion = colMap.containsKey("funcion") ? colMap.get("funcion") : colMap.get("rol");
            if (colFuncion == null) colFuncion = colMap.get("funcion/rol");
            if (colFuncion == null) colFuncion = colMap.get("rol / funcion");

            Integer colClase = colMap.containsKey("clase") ? colMap.get("clase") : colMap.get("clase academica");
            Integer colUnidad = colMap.get("unidad");
            Integer colFichaSalud = colMap.containsKey("ficha salud") ? colMap.get("ficha salud") : colMap.get("estado ficha salud");
            Integer colSeguro = colMap.containsKey("seguro") ? colMap.get("seguro") : colMap.get("seguro medico");
            if (colSeguro == null) colSeguro = colMap.get("estado seguro");
            Integer colAdhesionPadres = colMap.containsKey("adhesion padres") ? colMap.get("adhesion padres") : colMap.get("adhesion");
            if (colAdhesionPadres == null) colAdhesionPadres = colMap.get("estado adhesion padres");
            Integer colEstado = colMap.get("estado");

            if (colNombre == null || colApellido == null) {
                throw new RuntimeException("No se encontraron las columnas obligatorias 'Nombre' y 'Apellido' en la cabecera.");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String nombre = getCellStringValue(row.getCell(colNombre)).trim();
                String apellido = getCellStringValue(row.getCell(colApellido)).trim();

                if (nombre.isEmpty() || apellido.isEmpty()) continue;

                String funcion = colFuncion != null ? getCellStringValue(row.getCell(colFuncion)).trim() : "CONQUISTADOR";
                if (funcion.isEmpty()) funcion = "CONQUISTADOR";

                String nombreClaseVal = colClase != null ? getCellStringValue(row.getCell(colClase)).trim() : "";
                String nombreUnidadVal = colUnidad != null ? getCellStringValue(row.getCell(colUnidad)).trim() : "";

                String estadoFichaSalud = colFichaSalud != null ? getCellStringValue(row.getCell(colFichaSalud)).trim() : "PENDIENTE";
                if (estadoFichaSalud.isEmpty()) estadoFichaSalud = "PENDIENTE";

                String estadoSeguro = colSeguro != null ? getCellStringValue(row.getCell(colSeguro)).trim() : "NO_POSEE_SEGURO";
                if (estadoSeguro.isEmpty()) estadoSeguro = "NO_POSEE_SEGURO";

                if ("POSEE SEGURO".equalsIgnoreCase(estadoSeguro) || "PÓLIZA AL DÍA".equalsIgnoreCase(estadoSeguro) || "SI".equalsIgnoreCase(estadoSeguro)) {
                    estadoSeguro = "POSEE_SEGURO";
                } else if ("NO POSEE SEGURO".equalsIgnoreCase(estadoSeguro) || "SIN COBERTURA".equalsIgnoreCase(estadoSeguro) || "NO".equalsIgnoreCase(estadoSeguro)) {
                    estadoSeguro = "NO_POSEE_SEGURO";
                }

                String estadoAdhesionPadres = colAdhesionPadres != null ? getCellStringValue(row.getCell(colAdhesionPadres)).trim() : "PENDIENTE";
                if (estadoAdhesionPadres.isEmpty()) estadoAdhesionPadres = "PENDIENTE";

                String estado = colEstado != null ? getCellStringValue(row.getCell(colEstado)).trim() : "ACTIVO";
                if (estado.isEmpty()) estado = "ACTIVO";

                Optional<Miembro> miembroExistente = miembroRepository.findByClubIdClub(idClub).stream()
                        .filter(m -> m.getNombre().equalsIgnoreCase(nombre) && m.getApellido().equalsIgnoreCase(apellido))
                        .findFirst();

                Miembro miembro;
                if (miembroExistente.isPresent()) {
                    miembro = miembroExistente.get();
                } else {
                    miembro = new Miembro();
                    miembro.setClub(club);
                    miembro.setNombre(nombre);
                    miembro.setApellido(apellido);
                }

                Clase claseSeleccionada = claseDefecto;
                if (!nombreClaseVal.isEmpty()) {
                    for (Clase cl : clases) {
                        if (cl.getNombre().equalsIgnoreCase(nombreClaseVal)) {
                            claseSeleccionada = cl;
                            break;
                        }
                    }
                }
                miembro.setClase(claseSeleccionada);

                Unidad unidadSeleccionada = unidadDefecto;
                if (!nombreUnidadVal.isEmpty()) {
                    for (Unidad un : unidades) {
                        if (un.getNombre().equalsIgnoreCase(nombreUnidadVal)) {
                            unidadSeleccionada = un;
                            break;
                        }
                    }
                }
                miembro.setUnidad(unidadSeleccionada);

                miembro.setFuncion(funcion.toUpperCase());
                miembro.setEstado(estado.toUpperCase());
                miembro.setEstadoFichaSalud(estadoFichaSalud.toUpperCase());
                miembro.setEstadoSeguro(estadoSeguro.toUpperCase());
                miembro.setEstadoAdhesionPadres(estadoAdhesionPadres.toUpperCase());

                calcularPendientes(miembro);
                miembroRepository.save(miembro);
                autoAssignUnidadConsejero(miembro);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fallo al importar archivo Excel: " + e.getMessage(), e);
        }
    }

    public byte[] exportarExcel(Long idClub) throws IOException {
        List<Miembro> miembros = miembroRepository.findByClubIdClub(idClub);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Miembros");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            String[] columns = {
                "Nombre", "Apellido", "Rol / Función", "Clase", "Unidad",
                "Ficha Salud", "Seguro", "Adhesión Padres", "Estado", "Pendientes"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Miembro miembro : miembros) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(miembro.getNombre() != null ? miembro.getNombre() : "");
                row.createCell(1).setCellValue(miembro.getApellido() != null ? miembro.getApellido() : "");
                row.createCell(2).setCellValue(miembro.getFuncion() != null ? miembro.getFuncion() : "");
                row.createCell(3).setCellValue(miembro.getClase() != null && miembro.getClase().getNombre() != null ? miembro.getClase().getNombre() : "");
                row.createCell(4).setCellValue(miembro.getUnidad() != null && miembro.getUnidad().getNombre() != null ? miembro.getUnidad().getNombre() : "");
                row.createCell(5).setCellValue(miembro.getEstadoFichaSalud() != null ? miembro.getEstadoFichaSalud() : "PENDIENTE");
                row.createCell(6).setCellValue(miembro.getEstadoSeguro() != null ? miembro.getEstadoSeguro() : "NO_POSEE_SEGURO");
                row.createCell(7).setCellValue(miembro.getEstadoAdhesionPadres() != null ? miembro.getEstadoAdhesionPadres() : "PENDIENTE");
                row.createCell(8).setCellValue(miembro.getEstado() != null ? miembro.getEstado() : "ACTIVO");
                row.createCell(9).setCellValue(miembro.getPendientes() != null ? miembro.getPendientes() : 0);
            }

            DataValidationHelper dvHelper = sheet.getDataValidationHelper();

            String[] roles = {"CONQUISTADOR", "INSTRUCTOR", "CONSEJERO", "INSTRUCTOR,CONSEJERO", "SECRETARIO", "DIRECTOR"};
            DataValidationConstraint rolesConstraint = dvHelper.createExplicitListConstraint(roles);
            org.apache.poi.ss.util.CellRangeAddressList rolesRange =
                    new org.apache.poi.ss.util.CellRangeAddressList(1, 1000, 2, 2);
            DataValidation rolesValidation = dvHelper.createValidation(rolesConstraint, rolesRange);
            rolesValidation.setSuppressDropDownArrow(false);
            rolesValidation.setShowErrorBox(true);
            sheet.addValidationData(rolesValidation);

            String[] fichaSalud = {"ACTUALIZADA", "PENDIENTE"};
            DataValidationConstraint fichaSaludConstraint = dvHelper.createExplicitListConstraint(fichaSalud);
            org.apache.poi.ss.util.CellRangeAddressList fichaSaludRange =
                    new org.apache.poi.ss.util.CellRangeAddressList(1, 1000, 5, 5);
            DataValidation fichaSaludValidation = dvHelper.createValidation(fichaSaludConstraint, fichaSaludRange);
            fichaSaludValidation.setSuppressDropDownArrow(false);
            fichaSaludValidation.setShowErrorBox(true);
            sheet.addValidationData(fichaSaludValidation);

            String[] seguro = {"POSEE_SEGURO", "NO_POSEE_SEGURO"};
            DataValidationConstraint seguroConstraint = dvHelper.createExplicitListConstraint(seguro);
            org.apache.poi.ss.util.CellRangeAddressList seguroRange =
                    new org.apache.poi.ss.util.CellRangeAddressList(1, 1000, 6, 6);
            DataValidation seguroValidation = dvHelper.createValidation(seguroConstraint, seguroRange);
            seguroValidation.setSuppressDropDownArrow(false);
            seguroValidation.setShowErrorBox(true);
            sheet.addValidationData(seguroValidation);

            String[] adhesion = {"FIRMADA", "PENDIENTE"};
            DataValidationConstraint adhesionConstraint = dvHelper.createExplicitListConstraint(adhesion);
            org.apache.poi.ss.util.CellRangeAddressList adhesionRange =
                    new org.apache.poi.ss.util.CellRangeAddressList(1, 1000, 7, 7);
            DataValidation adhesionValidation = dvHelper.createValidation(adhesionConstraint, adhesionRange);
            adhesionValidation.setSuppressDropDownArrow(false);
            adhesionValidation.setShowErrorBox(true);
            sheet.addValidationData(adhesionValidation);

            String[] estado = {"ACTIVO", "INACTIVO"};
            DataValidationConstraint estadoConstraint = dvHelper.createExplicitListConstraint(estado);
            org.apache.poi.ss.util.CellRangeAddressList estadoRange =
                    new org.apache.poi.ss.util.CellRangeAddressList(1, 1000, 8, 8);
            DataValidation estadoValidation = dvHelper.createValidation(estadoConstraint, estadoRange);
            estadoValidation.setSuppressDropDownArrow(false);
            estadoValidation.setShowErrorBox(true);
            sheet.addValidationData(estadoValidation);

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            int[] minWidths = {
                18 * 256, // Nombre
                18 * 256, // Apellido
                20 * 256, // Rol / Función
                16 * 256, // Clase
                16 * 256, // Unidad
                16 * 256, // Ficha Salud
                18 * 256, // Seguro
                18 * 256, // Adhesión Padres
                14 * 256, // Estado
                12 * 256  // Pendientes
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

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDate date = cell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    return date.toString();
                }
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

    private void calcularPendientes(Miembro miembro) {
        int pendientesCount = 0;
        if ("PENDIENTE".equalsIgnoreCase(miembro.getEstadoFichaSalud())) pendientesCount++;
        if ("NO_POSEE_SEGURO".equalsIgnoreCase(miembro.getEstadoSeguro())) pendientesCount++;
        if ("PENDIENTE".equalsIgnoreCase(miembro.getEstadoAdhesionPadres())) pendientesCount++;
        
        // En una lógica real, validar si tiene clase asignada y especialidades registradas (RN-15)
        if (miembro.getClase() == null) pendientesCount++;
        
        miembro.setPendientes(pendientesCount);
    }
}
