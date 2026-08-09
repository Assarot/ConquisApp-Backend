package com.conquistadores.gestionclub.modules.poa.service;

import com.opencsv.CSVReader;
import com.conquistadores.gestionclub.modules.poa.model.Cronograma;
import com.conquistadores.gestionclub.modules.poa.model.BloqueCronograma;
import com.conquistadores.gestionclub.modules.poa.repository.CronogramaRepository;
import com.conquistadores.gestionclub.modules.poa.repository.BloqueCronogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
public class CronogramaService {

    @Autowired
    private CronogramaRepository cronogramaRepository;

    @Autowired
    private BloqueCronogramaRepository bloqueCronogramaRepository;

    public List<Cronograma> getCronogramasByClase(String idClase) {
        return cronogramaRepository.findByClaseIdClase(idClase);
    }

    public List<BloqueCronograma> getBloquesByCronograma(String idCronograma) {
        return bloqueCronogramaRepository.findByCronogramaIdCronograma(idCronograma);
    }

    @Transactional
    public BloqueCronograma registrarBloque(String idCronograma, BloqueCronograma bloque) {
        Cronograma cronograma = cronogramaRepository.findById(idCronograma)
                .orElseThrow(() -> new RuntimeException("Cronograma no encontrado"));

        bloque.setCronograma(cronograma);
        return bloqueCronogramaRepository.save(bloque);
    }

    @Transactional
    public BloqueCronograma actualizarBloque(String idBloque, BloqueCronograma request) {
        BloqueCronograma bloque = bloqueCronogramaRepository.findById(idBloque)
                .orElseThrow(() -> new RuntimeException("Bloque de cronograma no encontrado"));

        bloque.setHoraInicio(request.getHoraInicio());
        bloque.setHoraFin(request.getHoraFin());
        bloque.setTitulo(request.getTitulo());
        bloque.setDescripcion(request.getDescripcion());
        bloque.setTipo(request.getTipo());

        return bloqueCronogramaRepository.save(bloque);
    }

    @Transactional
    public void eliminarBloque(String idBloque) {
        BloqueCronograma bloque = bloqueCronogramaRepository.findById(idBloque)
                .orElseThrow(() -> new RuntimeException("Bloque de cronograma no encontrado"));
        bloqueCronogramaRepository.delete(bloque);
    }

    @Transactional
    public void importarCronogramaCsv(String idCronograma, MultipartFile file) {
        Cronograma cronograma = cronogramaRepository.findById(idCronograma)
                .orElseThrow(() -> new RuntimeException("Cronograma no encontrado"));

        // Delete existing blocks to reload
        List<BloqueCronograma> existing = bloqueCronogramaRepository.findByCronogramaIdCronograma(idCronograma);
        bloqueCronogramaRepository.deleteAll(existing);

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReader(reader);
            String[] nextLine;
            csvReader.readNext(); // Skip header
            
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length < 3) continue;
                String horaInicio = nextLine[0].trim();
                String horaFin = nextLine[1].trim();
                String titulo = nextLine[2].trim();
                String descripcion = nextLine.length > 3 ? nextLine[3].trim() : "";
                String tipo = nextLine.length > 4 ? nextLine[4].trim() : "FIXED";

                BloqueCronograma bloque = new BloqueCronograma();
                bloque.setCronograma(cronograma);
                bloque.setHoraInicio(horaInicio);
                bloque.setHoraFin(horaFin);
                bloque.setTitulo(titulo);
                bloque.setDescripcion(descripcion);
                bloque.setTipo(tipo);

                bloqueCronogramaRepository.save(bloque);
            }
        } catch (Exception e) {
            throw new RuntimeException("Fallo al importar cronograma CSV: " + e.getMessage(), e);
        }
    }
}
