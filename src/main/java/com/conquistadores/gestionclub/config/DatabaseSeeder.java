package com.conquistadores.gestionclub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Integer clubCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM clubes", Integer.class);
        if (clubCount != null && clubCount > 0) {
            return;
        }

        // 1. Seed Clubs
        jdbcTemplate.update(
                "INSERT INTO clubes (id_club, nombre, tipo, configuracion) VALUES (?, ?, ?, ?)",
                "uuid-club-conquistadores-orion", "Club Fernando Stahl", "CONQUISTADORES", "{\"distrito\": \"Iquitos Central\", \"mision\": \"Misión del Oriente Peruano (MOP)\", \"director\": \"Esteban Quito\"}"
        );
        jdbcTemplate.update(
                "INSERT INTO clubes (id_club, nombre, tipo, configuracion) VALUES (?, ?, ?, ?)",
                "uuid-club-orion-2", "Club Orión", "CONQUISTADORES", "{\"distrito\": \"Punchana\", \"mision\": \"Misión del Oriente Peruano (MOP)\", \"director\": \"Roberto Gómez\"}"
        );
        jdbcTemplate.update(
                "INSERT INTO clubes (id_club, nombre, tipo, configuracion) VALUES (?, ?, ?, ?)",
                "uuid-club-betel-3", "Club Betel", "CONQUISTADORES", "{\"distrito\": \"San Juan Bautista\", \"mision\": \"Misión del Oriente Peruano (MOP)\", \"director\": \"Patricia Dávila\"}"
        );

        // 2. Seed Roles (8 Roles)
        insertRol("uuid-rol-administrador", "ADMINISTRADOR");
        insertRol("uuid-rol-director", "DIRECTOR");
        insertRol("uuid-rol-secretario", "SECRETARIO");
        insertRol("uuid-rol-director-asociado", "DIRECTOR_ASOCIADO");
        insertRol("uuid-rol-instructor", "INSTRUCTOR");
        insertRol("uuid-rol-consejero", "CONSEJERO");
        insertRol("uuid-rol-conquistador", "CONQUISTADOR");
        insertRol("uuid-rol-padre", "PADRE");

        // 3. Seed Generic Users
        insertUsuario("uuid-user-admin", "Admin", "General", "admin@club.com", "admin123", "uuid-club-conquistadores-orion", "uuid-rol-administrador");
        insertUsuario("uuid-user-director", "Carlos", "Mendoza", "director@club.com", "director123", "uuid-club-conquistadores-orion", "uuid-rol-director");
        insertUsuario("uuid-user-secretario", "María", "López", "secretario@club.com", "secretario123", "uuid-club-conquistadores-orion", "uuid-rol-secretario");
        insertUsuario("uuid-user-asociado", "Jorge", "Ríos", "director.asociado@club.com", "asociado123", "uuid-club-conquistadores-orion", "uuid-rol-director-asociado");
        insertUsuario("uuid-user-instructor", "Juan", "Pérez", "instructor@club.com", "instructor123", "uuid-club-conquistadores-orion", "uuid-rol-instructor");
        insertUsuario("uuid-user-consejero", "Ricardo", "Gómez", "consejero@club.com", "consejero123", "uuid-club-conquistadores-orion", "uuid-rol-consejero");
        insertUsuario("uuid-user-conquistador", "Samuel", "Alvarado", "conquistador@club.com", "conquistador123", "uuid-club-conquistadores-orion", "uuid-rol-conquistador");
        insertUsuario("uuid-user-padre", "Elena", "Torres", "padre@club.com", "padre123", "uuid-club-conquistadores-orion", "uuid-rol-padre");
        insertUsuario("uuid-usuario-mock-001", "Esteban", "Quito", "esteban.quito@club.com", "director123", "uuid-club-conquistadores-orion", "uuid-rol-director");

        // 4. Seed Cuadernillos
        jdbcTemplate.update(
                "INSERT INTO versiones_cuadernillos (id_version_cuadernillo, numero_version, fecha_publicacion) VALUES (?, ?, ?)",
                "uuid-version-2026", "v2026", Date.valueOf(LocalDate.now())
        );

        // 5. Seed Classes
        insertClase("clase-amigo", "Amigo", "uuid-club-conquistadores-orion", "uuid-version-2026");
        insertClase("clase-companero", "Compañero", "uuid-club-conquistadores-orion", "uuid-version-2026");
        insertClase("clase-explorador", "Explorador", "uuid-club-conquistadores-orion", "uuid-version-2026");
        insertClase("clase-viajero", "Viajero", "uuid-club-conquistadores-orion", "uuid-version-2026");
        insertClase("clase-guia", "Guía", "uuid-club-conquistadores-orion", "uuid-version-2026");

        // 6. Seed Units
        insertUnidad("unidad-orion-1", "Halcones", "uuid-club-conquistadores-orion", "pets", "primary", "Unidad masculina de conquistadores.");
        insertUnidad("unidad-orion-2", "Águilas", "uuid-club-conquistadores-orion", "flight", "secondary", "Unidad femenina de conquistadoras.");
        insertUnidad("unidad-orion-3", "Leones", "uuid-club-conquistadores-orion", "local_fire_department", "tertiary", "Unidad masculina mayor.");
        insertUnidad("unidad-orion-4", "Estrellas", "uuid-club-conquistadores-orion", "star", "primary", "Unidad femenina mayor.");

        // 7. Seed Specialties
        insertEspecialidad("esp-1", "Nudos y Amarras", true, "uuid-club-conquistadores-orion", "HABILIDADES", 20, "Conocer y ejecutar 20 nudos reglamentarios, amarras cuadradas y diagonales.", "all_inclusive");
        insertEspecialidad("esp-2", "Primeros Auxilios I", true, "uuid-club-conquistadores-orion", "CIENCIA", 25, "Atención básica de shock, vendajes, RCP y manejo de fracturas simples.", "medical_services");
        insertEspecialidad("esp-3", "Árboles y Arbustos", false, "uuid-club-conquistadores-orion", "NATURALEZA", 15, "Identificar 15 especies de árboles nativos por corteza, hoja y frutos.", "park");
        insertEspecialidad("esp-4", "Campismo y Supervivencia", true, "uuid-club-conquistadores-orion", "RECREACION", 30, "Armado de refugios naturales, cocina al aire libre y orientación sin brújula.", "cabin");
        insertEspecialidad("esp-5", "Testificación Juvenil", false, "uuid-club-conquistadores-orion", "MISIONERAS", 20, "Participar activamente en proyectos comunitarios y visitas de servicio.", "diversity_3");
        insertEspecialidad("esp-6", "Astronomía", true, "uuid-club-conquistadores-orion", "NATURALEZA", 25, "Reconocer constelaciones principales del hemisferio sur y planetas visibles.", "bedtime");

        // 8. Seed Members
        insertMiembro("m-1", "Mateo", "Silva", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, "uuid-club-conquistadores-orion", "unidad-orion-2", "clase-amigo");
        insertMiembro("m-2", "Lucas", "Morales", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, "uuid-club-conquistadores-orion", "unidad-orion-2", "clase-amigo");
        insertMiembro("m-3", "Sofía", "Quispe", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, "uuid-club-conquistadores-orion", "unidad-orion-1", "clase-companero");
        insertMiembro("m-4", "Valentina", "Castro", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, "uuid-club-conquistadores-orion", "unidad-orion-1", "clase-companero");
        insertMiembro("m-5", "Daniel", "Rivas", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, "uuid-club-conquistadores-orion", "unidad-orion-3", "clase-explorador");
        insertMiembro("m-6", "Camila", "Benítez", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, "uuid-club-conquistadores-orion", "unidad-orion-4", "clase-guia");
    }

    private void insertRol(String id, String nombre) {
        jdbcTemplate.update(
                "INSERT INTO roles (id_rol, nombre) VALUES (?, ?)",
                id, nombre
        );
    }

    private void insertUsuario(String id, String nombre, String apellido, String email, String password, String idClub, String idRol) {
        jdbcTemplate.update(
                "INSERT INTO usuarios (id_usuario, nombre, apellido, email, password_hash, id_club, id_rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, nombre, apellido, email, passwordEncoder.encode(password), idClub, idRol, "ACTIVO"
        );
    }

    private void insertClase(String id, String nombre, String idClub, String idVersion) {
        jdbcTemplate.update(
                "INSERT INTO clases (id_clase, nombre, id_club, id_version_cuadernillo) VALUES (?, ?, ?, ?)",
                id, nombre, idClub, idVersion
        );
    }

    private void insertUnidad(String id, String nombre, String idClub, String icono, String color, String descripcion) {
        jdbcTemplate.update(
                "INSERT INTO unidades (id_unidad, nombre, id_club, icono, color, descripcion) VALUES (?, ?, ?, ?, ?, ?)",
                id, nombre, idClub, icono, color, descripcion
        );
    }

    private void insertEspecialidad(String id, String nombre, boolean requiereExamen, String idClub, String categoria, int puntos, String descripcion, String imagenUrl) {
        jdbcTemplate.update(
                "INSERT INTO especialidades (id_especialidad, nombre, requiere_examen, id_club, categoria, puntos_maestria, descripcion, imagen_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, nombre, requiereExamen, idClub, categoria, puntos, descripcion, imagenUrl
        );
    }

    private void insertMiembro(String id, String nombre, String apellido, String funcion, String estado, String salud, String seguro, String adhesion, int pendientes, String idClub, String idUnidad, String idClase) {
        jdbcTemplate.update(
                "INSERT INTO miembros (id_miembro, nombre, apellido, funcion, estado, estado_ficha_salud, estado_seguro, estado_adhesion_padres, pendientes, id_club, id_unidad, id_clase) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, nombre, apellido, funcion, estado, salud, seguro, adhesion, pendientes, idClub, idUnidad, idClase
        );
    }
}
