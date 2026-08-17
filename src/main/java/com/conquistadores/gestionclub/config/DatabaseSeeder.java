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

    private static final Long CLUB_FERNANDO_STAHL = 1L;
    private static final Long CLUB_ORION = 2L;
    private static final Long CLUB_BETEL = 3L;

    @Override
    public void run(String... args) throws Exception {
        // Drop NOT NULL constraints and alter columns dynamically on startup
        try {
            jdbcTemplate.execute("ALTER TABLE usuarios ALTER COLUMN id_club DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE clases ALTER COLUMN id_club DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE especialidades ALTER COLUMN id_club DROP NOT NULL");
            
            // 1. Categoria Especialidades
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS categorias_especialidades (" +
                    "id_categoria_especialidad SERIAL PRIMARY KEY, " +
                    "nombre VARCHAR(255) NOT NULL UNIQUE, " +
                    "tiene_maestria BOOLEAN NOT NULL DEFAULT TRUE)");
            
            // 2. Add columns to especialidades
            jdbcTemplate.execute("ALTER TABLE especialidades ADD COLUMN IF NOT EXISTS id_categoria_especialidad BIGINT REFERENCES categorias_especialidades(id_categoria_especialidad)");
            jdbcTemplate.execute("ALTER TABLE especialidades ADD COLUMN IF NOT EXISTS nivel_destreza INTEGER");
            jdbcTemplate.execute("ALTER TABLE especialidades ADD COLUMN IF NOT EXISTS ano_introduccion INTEGER");
            jdbcTemplate.execute("ALTER TABLE especialidades ALTER COLUMN categoria DROP NOT NULL");

            // 3. Add columns to requisitos
            jdbcTemplate.execute("ALTER TABLE requisitos ADD COLUMN IF NOT EXISTS id_clase BIGINT REFERENCES clases(id_clase)");
            jdbcTemplate.execute("ALTER TABLE requisitos ADD COLUMN IF NOT EXISTS id_especialidad BIGINT REFERENCES especialidades(id_especialidad)");
            jdbcTemplate.execute("ALTER TABLE requisitos ADD COLUMN IF NOT EXISTS es_avanzado BOOLEAN");
            jdbcTemplate.execute("ALTER TABLE requisitos ALTER COLUMN id_version_cuadernillo DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE requisitos ALTER COLUMN id_categoria DROP NOT NULL");
            
            System.out.println("DDL schema alterations executed successfully.");
        } catch (Exception e) {
            System.err.println("Warning: Could not execute database schema alterations: " + e.getMessage());
        }

        Integer clubCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM clubes", Integer.class);
        if (clubCount != null && clubCount > 0) {
            return;
        }

        // 1. Seed Clubs
        jdbcTemplate.update(
                "INSERT INTO clubes (id_club, nombre, tipo, region, configuracion) VALUES (?, ?, ?, ?, ?)",
                CLUB_FERNANDO_STAHL, "Club Fernando Stahl", "CONQUISTADORES", "Loreto",
                "{\"distrito\": \"Iquitos Central\", \"mision\": \"Misión del Oriente Peruano (MOP)\", \"director\": \"Esteban Quito\"}"
        );
        jdbcTemplate.update(
                "INSERT INTO clubes (id_club, nombre, tipo, region, configuracion) VALUES (?, ?, ?, ?, ?)",
                CLUB_ORION, "Club Orión", "CONQUISTADORES", "San Martín",
                "{\"distrito\": \"Punchana\", \"mision\": \"Misión del Oriente Peruano (MOP)\", \"director\": \"Roberto Gómez\"}"
        );
        jdbcTemplate.update(
                "INSERT INTO clubes (id_club, nombre, tipo, region, configuracion) VALUES (?, ?, ?, ?, ?)",
                CLUB_BETEL, "Club Betel", "CONQUISTADORES", "Ucayali",
                "{\"distrito\": \"San Juan Bautista\", \"mision\": \"Misión del Oriente Peruano (MOP)\", \"director\": \"Patricia Dávila\"}"
        );

        // 2. Seed Roles (8 Roles)
        insertRol(1L, "ADMINISTRADOR");
        insertRol(2L, "DIRECTOR");
        insertRol(3L, "SECRETARIO");
        insertRol(4L, "DIRECTOR_ASOCIADO");
        insertRol(5L, "INSTRUCTOR");
        insertRol(6L, "CONSEJERO");
        insertRol(7L, "CONQUISTADOR");
        insertRol(8L, "PADRE");

        // 3. Seed Generic Users
        insertUsuario(1L, "Admin", "General", "admin@club.com", "admin123", null, 1L);
        insertUsuario(2L, "Carlos", "Mendoza", "director@club.com", "director123", CLUB_FERNANDO_STAHL, 2L);
        insertUsuario(3L, "María", "López", "secretario@club.com", "secretario123", CLUB_FERNANDO_STAHL, 3L);
        insertUsuario(4L, "Jorge", "Ríos", "director.asociado@club.com", "asociado123", CLUB_FERNANDO_STAHL, 4L);
        insertUsuario(5L, "Juan", "Pérez", "instructor@club.com", "instructor123", CLUB_FERNANDO_STAHL, 5L);
        insertUsuario(6L, "Ricardo", "Gómez", "consejero@club.com", "consejero123", CLUB_FERNANDO_STAHL, 6L);
        insertUsuario(7L, "Samuel", "Alvarado", "conquistador@club.com", "conquistador123", CLUB_FERNANDO_STAHL, 7L);
        insertUsuario(8L, "Elena", "Torres", "padre@club.com", "padre123", CLUB_FERNANDO_STAHL, 8L);
        insertUsuario(9L, "Esteban", "Quito", "esteban.quito@club.com", "director123", CLUB_FERNANDO_STAHL, 2L);

        // 4. Seed Cuadernillos
        jdbcTemplate.update(
                "INSERT INTO versiones_cuadernillos (id_version_cuadernillo, numero_version, fecha_publicacion) VALUES (?, ?, ?)",
                1L, "v2026", Date.valueOf(LocalDate.now())
        );

        // 5. Seed Classes — Las 6 clases básicas reales del club de Conquistadores
        insertClase(1L, "Amigo", null, 1L);
        insertClase(2L, "Compañero", null, 1L);
        insertClase(3L, "Explorador", null, 1L);
        insertClase(4L, "Pionero", null, 1L);
        insertClase(5L, "Excursionista", null, 1L);
        insertClase(6L, "Guía", null, 1L);

        // 6. Seed Units
        insertUnidad(1L, "Halcones", CLUB_FERNANDO_STAHL, "pets", "primary", "Unidad masculina de conquistadores.");
        insertUnidad(2L, "Águilas", CLUB_FERNANDO_STAHL, "flight", "secondary", "Unidad femenina de conquistadoras.");
        insertUnidad(3L, "Leones", CLUB_FERNANDO_STAHL, "local_fire_department", "tertiary", "Unidad masculina mayor.");
        insertUnidad(4L, "Estrellas", CLUB_FERNANDO_STAHL, "star", "primary", "Unidad femenina mayor.");

        // 6.5 Seed Specialty Categories
        insertCategoriaEspecialidad(1L, "Habilidades Manuales", true);
        insertCategoriaEspecialidad(2L, "Salud", true);
        insertCategoriaEspecialidad(3L, "Naturaleza", true);
        insertCategoriaEspecialidad(4L, "Recreación", true);
        insertCategoriaEspecialidad(5L, "Misioneras", false);
        insertCategoriaEspecialidad(6L, "Artes Domésticas", false);

        // 7. Seed Specialties
        insertEspecialidad(1L, "Nudos y Amarras", true, null, 1L, 1, 1975, 20, "Conocer y ejecutar 20 nudos reglamentarios, amarras cuadradas y diagonales.", "all_inclusive");
        insertEspecialidad(2L, "Primeros Auxilios - Avanzado", true, null, 2L, 2, 1980, 25, "Atención de shock, vendajes, RCP, manejo de fracturas y traslado de heridos.", "medical_services");
        insertEspecialidad(3L, "Aves", false, null, 3L, 1, 1990, 15, "Identificar 20 especies de aves nativas por canto, plumaje y hábitat.", "flutter_dash");
        insertEspecialidad(4L, "Campamento", true, null, 4L, 2, 1970, 30, "Armado de campamentos, seguridad en el fuego, cocina al aire libre y liderazgo en salidas.", "cabin");
        insertEspecialidad(5L, "Evangelismo Personal", false, null, 5L, 1, 2000, 20, "Participar en un proyecto de testificación personal y presentar un estudio bíblico.", "diversity_3");
        insertEspecialidad(6L, "Astronomía", true, null, 3L, 2, 1972, 25, "Reconocer constelaciones principales del hemisferio sur, fases lunares y planetas visibles.", "bedtime");
        insertEspecialidad(7L, "Cocina Básica", false, null, 6L, 1, 1985, 15, "Preparar y planificar un menú balanceado, normas de higiene y manejo de alimentos.", "skillet");
        insertEspecialidad(8L, "Orientación", true, null, 4L, 2, 1978, 20, "Uso de brújula y mapa, cálculo de rumbos y recorrido de una pista de orientación.", "explore");

        // 8. Seed Members
        insertMiembro(1L, "Mateo", "Silva", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 2L, 1L);
        insertMiembro(2L, "Lucas", "Morales", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 2L, 1L);
        insertMiembro(3L, "Sofía", "Quispe", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 1L, 2L);
        insertMiembro(4L, "Valentina", "Castro", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 1L, 2L);
        insertMiembro(5L, "Daniel", "Rivas", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 3L, 3L);
        insertMiembro(6L, "Camila", "Benítez", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 4L, 4L);
        insertMiembro(7L, "Adrián", "Vásquez", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 3L, 5L);
        insertMiembro(8L, "Renata", "Flores", "CONQUISTADOR", "ACTIVO", "ACTUALIZADA", "POSEE_SEGURO", "FIRMADA", 0, CLUB_FERNANDO_STAHL, 4L, 6L);
        syncSequences();
    }

    private void syncSequences() {
        try {
            jdbcTemplate.execute("SELECT setval('usuarios_id_usuario_seq', COALESCE((SELECT MAX(id_usuario) FROM usuarios), 1))");
            jdbcTemplate.execute("SELECT setval('miembros_id_miembro_seq', COALESCE((SELECT MAX(id_miembro) FROM miembros), 1))");
            jdbcTemplate.execute("SELECT setval('clubes_id_club_seq', COALESCE((SELECT MAX(id_club) FROM clubes), 1))");
            jdbcTemplate.execute("SELECT setval('unidades_id_unidad_seq', COALESCE((SELECT MAX(id_unidad) FROM unidades), 1))");
            jdbcTemplate.execute("SELECT setval('clases_id_clase_seq', COALESCE((SELECT MAX(id_clase) FROM clases), 1))");
            jdbcTemplate.execute("SELECT setval('roles_id_rol_seq', COALESCE((SELECT MAX(id_rol) FROM roles), 1))");
            jdbcTemplate.execute("SELECT setval('categorias_especialidades_id_categoria_especialidad_seq', COALESCE((SELECT MAX(id_categoria_especialidad) FROM categorias_especialidades), 1))");
            jdbcTemplate.execute("SELECT setval('especialidades_id_especialidad_seq', COALESCE((SELECT MAX(id_especialidad) FROM especialidades), 1))");
            System.out.println("PostgreSQL sequences successfully synchronized after database seeding.");
        } catch (Exception e) {
            System.err.println("Warning: Could not sync PostgreSQL sequences: " + e.getMessage());
        }
    }

    private void insertRol(Long id, String nombre) {
        jdbcTemplate.update(
                "INSERT INTO roles (id_rol, nombre) VALUES (?, ?)",
                id, nombre
        );
    }

    private void insertUsuario(Long id, String nombre, String apellido, String email, String password, Long idClub, Long idRol) {
        jdbcTemplate.update(
                "INSERT INTO usuarios (id_usuario, nombre, apellido, email, password_hash, id_club, id_rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, nombre, apellido, email, passwordEncoder.encode(password), idClub, idRol, "ACTIVO"
        );
    }

    private void insertClase(Long id, String nombre, Long idClub, Long idVersion) {
        jdbcTemplate.update(
                "INSERT INTO clases (id_clase, nombre, id_club, id_version_cuadernillo) VALUES (?, ?, ?, ?)",
                id, nombre, idClub, idVersion
        );
    }

    private void insertUnidad(Long id, String nombre, Long idClub, String icono, String color, String descripcion) {
        jdbcTemplate.update(
                "INSERT INTO unidades (id_unidad, nombre, id_club, icono, color, descripcion) VALUES (?, ?, ?, ?, ?, ?)",
                id, nombre, idClub, icono, color, descripcion
        );
    }

    private void insertCategoriaEspecialidad(Long id, String nombre, boolean tieneMaestria) {
        jdbcTemplate.update(
                "INSERT INTO categorias_especialidades (id_categoria_especialidad, nombre, tiene_maestria) VALUES (?, ?, ?)",
                id, nombre, tieneMaestria
        );
    }

    private void insertEspecialidad(Long id, String nombre, boolean requiereExamen, Long idClub, Long idCategoria, Integer nivel, Integer ano, int puntos, String descripcion, String imagenUrl) {
        jdbcTemplate.update(
                "INSERT INTO especialidades (id_especialidad, nombre, requiere_examen, id_club, id_categoria_especialidad, nivel_destreza, ano_introduccion, puntos_maestria, descripcion, imagen_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, nombre, requiereExamen, idClub, idCategoria, nivel, ano, puntos, descripcion, imagenUrl
        );
    }

    private void insertMiembro(Long id, String nombre, String apellido, String funcion, String estado, String salud, String seguro, String adhesion, int pendientes, Long idClub, Long idUnidad, Long idClase) {
        jdbcTemplate.update(
                "INSERT INTO miembros (id_miembro, nombre, apellido, funcion, estado, estado_ficha_salud, estado_seguro, estado_adhesion_padres, pendientes, id_club, id_unidad, id_clase) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, nombre, apellido, funcion, estado, salud, seguro, adhesion, pendientes, idClub, idUnidad, idClase
        );
    }
}