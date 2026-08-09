# Diseño y Funcionamiento del Backend — Sistema de Gestión del Club de Conquistadores (Spring Boot)

Este documento detalla la propuesta técnica definitiva para el desarrollo del **Backend** del proyecto, utilizando el framework **Spring Boot (Java)** y la arquitectura **MVC** para la exposición de una API REST. Además, se describe la estrategia de contenedorización con **Docker** para aislar el monolito y la base de datos **PostgreSQL**.

---

## 1. ¿Qué se va a desarrollar?

Se desarrollará una **API RESTful monolítica y modular** construida con **Java (versión 17 o superior)** y **Spring Boot 3.x**. El sistema utilizará **Spring Data JPA** (con Hibernate) para la persistencia en **PostgreSQL**, y se empaquetará de forma independiente en contenedores **Docker**.

El backend seguirá el patrón clásico de **Arquitectura MVC (Model-View-Controller)** adaptado a APIs REST:

*   **View (Vista / Capa de Exposición):** Representada por los Controladores REST (`@RestController`) que exponen recursos JSON y consumen peticiones HTTP.
*   **Controller / Service (Lógica de Control y Negocio):** Clases `@Service` que procesan las reglas de negocio, coordinan las transacciones y ejecutan las transformaciones de datos.
*   **Model (Modelo / Capa de Persistencia):** Representado por las Entidades JPA (`@Entity`) que mapean las tablas físicas de la base de datos y las interfaces de acceso a datos (`@Repository`).

---

## 2. Estructura de Paquetes en el Monolito MVC

La estructura del proyecto seguirá la convención estándar de Spring Boot, organizada por capas y módulos funcionales:

```
src/main/java/com/conquistadores/gestionclub/
├── config/                  # Configuraciones globales (Seguridad, JPA, Docker profiles)
├── exception/               # Manejo global de excepciones (@ControllerAdvice)
├── security/                # Configuración de Spring Security y filtros JWT
├── modules/                 # Módulos del sistema
│   ├── auth/                # Módulo de Autenticación
│   ├── poa/                 # Módulo de Plan Operativo Anual
│   ├── miembros/            # Módulo de Miembros, Seguro y Fichas Médicas
│   ├── avances/             # Módulo de Gestión de Avances y Calificaciones
│   ├── asistencia/          # Módulo de Registro de Asistencia
│   └── auditoria/           # Módulo de Historial y Auditoría (AOP / Entity Listeners)
│       ├── controller/      # @RestController (Exposición REST)
│       ├── service/         # @Service (Lógica de negocio e interfaces)
│       ├── repository/      # @Repository (Spring Data JPA)
│       ├── model/           # @Entity (Modelos de datos relacionales)
│       └── dto/             # Data Transfer Objects (Request/Response)
```

---

## 3. Justificación del Stack Tecnológico

### A. Java y Spring Boot
*   **Tipado y Robustez:** Java provee una fuerte tipificación estática y es líder en el desarrollo de sistemas robustos a nivel empresarial.
*   **Ecosistema Spring Boot:** Facilita la inyección de dependencias, la gestión de transacciones con `@Transactional` y cuenta con **Spring Security**, que es el estándar de la industria para resolver esquemas RBAC complejos (RN-32, RN-33, RN-45).

### B. Spring Data JPA (Hibernate)
*   Abstrae la escritura de consultas SQL repetitivas mediante métodos de consulta automática (`findBy...`).
*   Hibernate gestiona de manera transparente las relaciones muchos-a-muchos (N:M) y maneja las llaves foráneas requeridas por la integridad referencial (RN-02).

### C. Contenedores Docker (Aislamiento Completo)
*   **Dockerizar la Base de Datos:** Permite tener una instancia de PostgreSQL idéntica a producción sin instalar bases de datos locales, garantizando la portabilidad.
*   **Dockerizar el Monolito Spring Boot:** Aísla la aplicación, asegurando que corra exactamente igual en cualquier entorno de desarrollo o producción, sin preocuparse por versiones del JDK local del host.

---

## 4. ¿Cómo funcionará el Backend? (Diseño Detallado)

### A. Multi-tenant / Multi-club Lógico (RN-35 / RN-36)
Para aislar lógicamente los clubes dentro de una misma base de datos relacional:
1.  Se utilizará la funcionalidad de filtros dinámicos de Hibernate (`@FilterDef` y `@Filter`) en cada entidad que contenga el atributo `idClub`.
2.  Un filtro HTTP o un interceptor interceptará la petición, leerá el JWT, obtendrá el `id_club` del usuario logueado, y activará el filtro de Hibernate en la sesión actual de base de datos antes de ejecutar la consulta.

```java
// Ejemplo de configuración de Filtro en Entidad Miembro
@Entity
@Table(name = "miembros")
@FilterDef(name = "clubFilter", parameters = @ParamDef(name = "clubId", type = String.class))
@Filter(name = "clubFilter", condition = "id_club = :clubId")
public class Miembro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idMiembro;
    
    @Column(name = "id_club")
    private String idClub;
    // ... otros campos
}
```

### B. Seguridad: Autenticación JWT y RBAC (RN-32 / RN-33)
*   **Spring Security** se configurará para deshabilitar las sesiones por defecto (arquitectura Stateless) y requerir un token JWT en la cabecera `Authorization`.
*   El control de accesos se gestionará a nivel de método en los controladores utilizando la anotación `@PreAuthorize`:

```java
@RestController
@RequestMapping("/api/v1/avances")
public class AvanceController {

    @Autowired
    private AvanceService avanceService;

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'DIRECTOR', 'SECRETARIO') or (hasRole('INSTRUCTOR') and @securityService.isInstructorOfClass(#id))")
    public ResponseEntity<AvanceDTO> corregirAvance(@PathVariable String id, @RequestBody AvanceDTO dto) {
        return ResponseEntity.ok(avanceService.corregirAvance(id, dto));
    }
}
```

### C. Sincronización Asíncrona del POA (RN-03)
Cuando una actividad del POA cambie de fecha:
1.  `PoaService` publicará un evento de Spring (`ApplicationEvent`).
2.  Un componente oyente (`@EventListener`) anotado con `@Async` capturará el evento y actualizará de forma no bloqueante las sesiones y cronogramas afectados.

### D. Auditoría mediante JPA Entity Listeners (RN-37 / RN-42)
Se implementará una clase `@EntityListeners` para registrar automáticamente los cambios de estado en la base de datos:

```java
public class AuditListener {
    @PostUpdate
    public void afterUpdate(Object entity) {
        // Lógica para comparar campos modificados y persistir en la tabla de auditoría
        // utilizando Spring AOP o inyección manual de AuditService.
    }
}
```

### E. Módulo de Importación CSV (RN-21)
*   Se usará la librería `OpenCSV` dentro de un servicio transaccional.
*   El archivo CSV se procesará mediante buffers para no saturar la memoria RAM.
*   Se usará la cláusula de persistencia en lotes (batch inserts/updates) para actualizar los datos administrativos de los miembros sin afectar sus avances académicos.

---

## 5. Diseño de Contenedores y Orquestación (Docker)

Se utilizará una configuración basada en **Docker Compose** para levantar el entorno completo con un solo comando.

### A. Dockerfile para la Aplicación Spring Boot (Multi-Stage Build)
Se propone un archivo `Dockerfile` optimizado en dos fases (compilación y ejecución) para mantener la imagen final ligera y segura:

```dockerfile
# Fase 1: Compilación
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Fase 2: Ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### B. Archivo `docker-compose.yml` para Orquestación
Este archivo coordinará el contenedor del backend con el de la base de datos PostgreSQL, configurando redes internas privadas y volúmenes persistentes.

```yaml
version: '3.8'

services:
  postgres-db:
    image: postgres:15-alpine
    container_name: club-postgres-db
    environment:
      POSTGRES_DB: gestion_club_db
      POSTGRES_USER: club_admin
      POSTGRES_PASSWORD: admin_password_secure
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - club-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U club_admin -d gestion_club_db"]
      interval: 5s
      timeout: 5s
      retries: 5

  spring-backend:
    build: .
    container_name: club-spring-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-db:5432/gestion_club_db
      SPRING_DATASOURCE_USERNAME: club_admin
      SPRING_DATASOURCE_PASSWORD: admin_password_secure
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      postgres-db:
        condition: service_healthy
    networks:
      - club-network

volumes:
  postgres_data:

networks:
  club-network:
    driver: bridge
```
