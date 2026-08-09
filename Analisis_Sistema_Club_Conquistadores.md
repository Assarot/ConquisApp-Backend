# Análisis Técnico — Sistema de Gestión del Club de Conquistadores

Documento derivado de las Reglas de Negocio (RN-01 a RN-45) proporcionadas. Incluye: resumen de reglas de negocio, casos de uso, diseño de base de datos (arquitectura monolítica), consideraciones de escalabilidad y consideraciones de seguridad.

---

## 1. Resumen de Reglas de Negocio

| Código | Nombre | Módulos afectados | Prioridad |
|---|---|---|---|
| RN-01 | Gestión dinámica del POA | POA, Cronogramas, Dashboard | Alta |
| RN-02 | El POA es la fuente oficial de planificación | POA, Cronogramas, Dashboard | Alta |
| RN-03 | Sincronización automática del cronograma | POA, Cronogramas | Alta |
| RN-04 | Visualización completa del POA | POA, Cronogramas | Alta |
| RN-05 | Carga inicial del POA | POA | Alta |
| RN-06 | Plantilla única de cronograma | Cronogramas | Alta |
| RN-07 | Cobertura múltiple de requisitos | Sesiones, Avances | Alta |
| RN-08 | Definición del contenido de las sesiones | Sesiones | Alta |
| RN-09 | Requisitos organizados por categorías | Requisitos, Avances | Media |
| RN-10 | Administración de requisitos oficiales | Requisitos, Administración | Alta |
| RN-11 | Especialidades obligatorias y adicionales | Especialidades, Planificación, Cronogramas | Alta |
| RN-12 | Desarrollo de una especialidad | Especialidades, Sesiones | Alta |
| RN-13 | Evaluación de especialidades | Especialidades | Media |
| RN-14 | Material de apoyo para especialidades | Especialidades, Materiales | Media |
| RN-15 | Campo "Pendientes" | Miembros, Importación | Media |
| RN-16 | Campo "Función" | Miembros | Media |
| RN-17 | Estado de la ficha de salud | Miembros | Alta |
| RN-18 | Estado del seguro | Miembros | Alta |
| RN-19 | Adhesión de padres | Miembros | Alta |
| RN-20 | Estado del miembro | Miembros, Historial | Alta |
| RN-21 | Actualización de miembros mediante importación | Importación, Miembros | Alta |
| RN-22 | Cambio de unidad | Miembros, Unidades | Alta |
| RN-23 | Conservación del historial del conquistador | Historial, Reportes | Alta |
| RN-24 | Inactivación de miembros | Miembros, Historial | Alta |
| RN-25 | Gestión de avances individuales | Avances | Alta |
| RN-26 | Corrección de avances | Avances, Auditoría | Alta |
| RN-27 | Versionado del cuadernillo | Requisitos, Clases | Alta |
| RN-28 | Registro de asistencia | Asistencia, Sesiones | Alta |
| RN-29 | Gestión documental | Materiales, Especialidades, Clases | Media |
| RN-30 | Permisos para la administración de materiales | Materiales, Seguridad, Usuarios | Alta |
| RN-31 | Administración de sesiones por instructor | Sesiones, Seguridad, Usuarios | Alta |
| RN-32 | Gestión de roles del sistema | Seguridad, Usuarios | Alta |
| RN-33 | Restricción de permisos según rol | Seguridad, Usuarios | Alta |
| RN-34 | Dashboard administrativo | Dashboard, POA, Cronogramas | Media |
| RN-35 | Escalabilidad del sistema | Arquitectura, Configuración | Media |
| RN-36 | Soporte para Club de Líderes | Arquitectura, Configuración | Media |
| RN-37 | Registro de auditoría | Auditoría | Alta |
| RN-38 | Indicadores de gestión | Reportes, Dashboard, Auditoría | Media |
| RN-39 | Ranking de unidades | Ranking, Reportes | Media |
| RN-40 | Notificaciones del sistema | Notificaciones | Media |
| RN-41 | Reporte de avance para padres | Reportes, Usuarios | Media |
| RN-42 | Historial de modificaciones | Auditoría, Reportes | Alta |
| RN-45 | Restricción de modificación de avances | Gestión de Avances, Seguridad, Usuarios, Auditoría | Alta |

### Módulos identificados
POA · Cronogramas · Sesiones · Requisitos · Especialidades · Materiales · Miembros · Unidades · Historial · Avances · Asistencia · Importación · Auditoría · Reportes · Dashboard · Ranking · Notificaciones · Seguridad/Usuarios · Arquitectura/Configuración

### Actores identificados
Administrador · Director · Director Asociado · Secretario · Instructor · Consejero · Conquistador · Padre de familia

---

## 2. Casos de Uso

### 2.1 Actor: Administrador

- **CU-01 Gestionar requisitos oficiales del cuadernillo** (RN-10, RN-27): crear/actualizar requisitos y registrar nuevas versiones del cuadernillo sin alterar avances ya registrados.
- **CU-02 Gestionar catálogo de especialidades y material de apoyo** (RN-14): asociar fichas, documentos, imágenes, enlaces y modelos de evaluación a cada especialidad.
- **CU-03 Administrar roles y permisos** (RN-32, RN-33): crear/editar roles y asignar permisos por módulo.
- **CU-04 Configurar el sistema para múltiples clubes / Club de Líderes** (RN-35, RN-36): definir clases, requisitos y especialidades independientes por club.
- **CU-05 Consultar auditoría e indicadores globales** (RN-37, RN-38, RN-42).
- **CU-06 Gestionar todos los materiales del repositorio** (RN-30).

### 2.2 Actor: Director / Director Asociado

- **CU-07 Gestionar el POA** (RN-01, RN-02, RN-05): crear, editar, eliminar y actualizar actividades; cargar el POA inicial mediante plantilla.
- **CU-08 Visualizar Dashboard administrativo** (RN-34, RN-38): consultar actividades por clase, fecha, unidad, responsable y estado.
- **CU-09 Autorizar cambio de unidad de un conquistador** (RN-22).
- **CU-10 Consultar/corregir avances de cualquier clase** (RN-26, RN-45).
- **CU-11 Consultar ranking de unidades** (RN-39).
- **CU-12 Gestionar materiales con alcance global** (RN-30) — Director/Secretario.
- **CU-13 Administrar módulos asignados** (Director Asociado, RN-33): alcance restringido a los módulos delegados.

### 2.3 Actor: Secretario

- **CU-14 Registrar y editar el POA** (RN-01, RN-05).
- **CU-15 Importar/actualizar miembros desde el Sistema de Gestión de Clubes** (RN-21): cargar CSV, evitar duplicados, conservar avances/asistencias/historial.
- **CU-16 Gestionar datos administrativos del miembro**: función (RN-16), ficha de salud (RN-17), seguro (RN-18), adhesión de padres (RN-19), campo Pendientes (RN-15).
- **CU-17 Inactivar/reactivar miembro** (RN-20, RN-24): conservando historial.
- **CU-18 Registrar cambio de unidad** (RN-22).
- **CU-19 Consultar reportes e indicadores** (RN-38, RN-39).

### 2.4 Actor: Instructor

- **CU-20 Administrar sesiones de su clase** (RN-06, RN-08, RN-31): definir duración, actividades, materiales, evaluación, requisitos desarrollados y especialidades relacionadas; solo de la clase asignada.
- **CU-21 Registrar cobertura de requisitos en una sesión** (RN-07): una sesión puede cubrir uno o varios requisitos; un requisito puede completarse en varias sesiones.
- **CU-22 Desarrollar especialidades a lo largo de varias sesiones** (RN-11, RN-12, RN-13).
- **CU-23 Registrar y corregir avances de sus conquistadores** (RN-25, RN-26, RN-45): restringido a su(s) clase(s) asignada(s).
- **CU-24 Registrar asistencia de sesión** (RN-28): conquistadores, instructores, consejeros y demás líderes.
- **CU-25 Subir material de apoyo de su clase/especialidad** (RN-29, RN-30).

### 2.5 Actor: Consejero

- **CU-26 Registrar asistencia de su unidad** (RN-28).
- **CU-27 Administrar información de su unidad** (RN-33): alcance restringido a su unidad.

### 2.6 Actor: Conquistador

- **CU-28 Consultar su información personal, avances y materiales autorizados** (RN-33, RN-30).

### 2.7 Actor: Padre de familia

- **CU-29 Consultar el progreso académico de su(s) hijo(s)** (RN-41): porcentaje de avance, requisitos completados/pendientes, especialidades desarrolladas, asistencia — únicamente de sus hijos.

### 2.8 Casos de uso transversales (sistema)

- **CU-30 Sincronizar cronogramas automáticamente al modificar el POA** (RN-03).
- **CU-31 Visualizar todas las actividades del POA en los cronogramas** (RN-04).
- **CU-32 Generar cronograma automáticamente a partir de la plantilla única y el POA** (RN-06).
- **CU-33 Registrar auditoría de toda acción relevante** (RN-37, RN-42).
- **CU-34 Generar notificaciones internas** (RN-40).
- **CU-35 Calcular ranking de unidades** (RN-39): método de cálculo configurable.
- **CU-36 Generar indicadores de gestión** (RN-38): automáticos, a partir de datos ya registrados.

---

## 3. Diseño de Base de Datos (Arquitectura Monolítica)

Modelo relacional único (una sola base de datos, un solo despliegue de backend), pensado para escalar horizontalmente vía particionamiento lógico por `club_id` (ver sección 4). Se listan las entidades principales, sus campos clave y relaciones.

### 3.1 Módulo Seguridad / Usuarios

**usuario**
- `id_usuario` PK
- `id_club` FK → club (RN-35)
- `nombre`, `apellido`, `email`, `password_hash`
- `id_rol` FK → rol
- `estado` (activo/inactivo)

**rol** (RN-32, RN-33)
- `id_rol` PK
- `nombre` (Administrador, Director, Secretario, Director Asociado, Instructor, Consejero, Conquistador, Padre de familia)

**permiso**
- `id_permiso` PK
- `id_rol` FK
- `modulo`
- `accion` (crear, leer, editar, eliminar)

**instructor_clase** (relación N:M, RN-31)
- `id_usuario` FK, `id_clase` FK

**consejero_unidad** (RN-33)
- `id_usuario` FK, `id_unidad` FK

**padre_conquistador** (RN-41)
- `id_usuario_padre` FK, `id_miembro` FK

### 3.2 Módulo Club / Unidades / Clases

**club** (RN-35, RN-36)
- `id_club` PK, `nombre`, `tipo` (Conquistadores / Líderes), `configuracion` (JSON)

**unidad**
- `id_unidad` PK, `id_club` FK, `nombre`

**clase**
- `id_clase` PK, `id_club` FK, `nombre`, `id_version_cuadernillo` FK

### 3.3 Módulo Miembros

**miembro** (RN-15 a RN-24)
- `id_miembro` PK, `id_club` FK, `id_unidad` FK, `id_clase` FK
- `nombre`, `apellido`, `funcion` (RN-16)
- `estado` (activo/inactivo) (RN-20, RN-24)
- `estado_ficha_salud` (actualizada/pendiente) (RN-17)
- `estado_seguro` (posee/no posee) (RN-18)
- `estado_adhesion_padres` (firmada/pendiente) (RN-19)
- `pendientes` (calculado, RN-15)

**historial_unidad** (RN-22)
- `id_miembro` FK, `id_unidad_origen` FK, `id_unidad_destino` FK, `fecha_cambio`

**historial_academico** (RN-23)
- `id_miembro` FK, `id_clase` FK, `anio`, `especialidades_obtenidas`, `requisitos_completados`, `asistencia_resumen`

### 3.4 Módulo POA / Cronogramas

**poa** (RN-01, RN-02, RN-05)
- `id_poa` PK, `id_club` FK, `anio`, `estado`

**actividad_poa** (RN-01, RN-04)
- `id_actividad` PK, `id_poa` FK, `nombre`, `fecha`, `ambito` (club/iglesia/región/asociación/recurrente), `responsable`

**cronograma** (RN-03, RN-06)
- `id_cronograma` PK, `id_clase` FK, `id_actividad` FK (nullable si es generado por plantilla)
- `plantilla_base` (referencia a plantilla única)

### 3.5 Módulo Requisitos / Especialidades / Sesiones

**categoria_requisito** (RN-09)
- `id_categoria` PK, `nombre`

**requisito** (RN-09, RN-10, RN-27)
- `id_requisito` PK, `id_categoria` FK, `id_version_cuadernillo` FK, `descripcion`

**version_cuadernillo** (RN-27)
- `id_version_cuadernillo` PK, `numero_version`, `fecha_publicacion`

**especialidad** (RN-11 a RN-14)
- `id_especialidad` PK, `nombre`, `requiere_examen` (bool, RN-13), `id_club` FK

**especialidad_clase** (RN-11)
- `id_especialidad` FK, `id_clase` FK, `tipo` (obligatoria/adicional)

**sesion** (RN-06, RN-08, RN-31)
- `id_sesion` PK, `id_clase` FK, `id_instructor` FK, `fecha`, `duracion`, `actividades`, `materiales`, `evaluacion`

**sesion_requisito** (N:M, RN-07)
- `id_sesion` FK, `id_requisito` FK, `porcentaje_avance_sesion`

**sesion_especialidad** (N:M, RN-08, RN-12)
- `id_sesion` FK, `id_especialidad` FK

### 3.6 Módulo Avances / Asistencia

**avance** (RN-25, RN-26, RN-45)
- `id_avance` PK, `id_miembro` FK, `id_requisito` FK, `estado` (pendiente/en progreso/completado), `fecha_actualizacion`, `id_instructor_responsable` FK

**asistencia** (RN-28)
- `id_asistencia` PK, `id_sesion` FK, `id_usuario` FK (conquistador/instructor/consejero/líder), `estado` (presente/ausente/justificado)

### 3.7 Módulo Materiales

**material** (RN-14, RN-29, RN-30)
- `id_material` PK, `tipo` (PDF, Word, imagen, enlace, video), `url_o_archivo`
- `id_especialidad` FK (nullable), `id_clase` FK (nullable), `id_sesion` FK (nullable)
- `id_usuario_creador` FK

### 3.8 Módulo Auditoría / Notificaciones / Ranking

**auditoria** (RN-26, RN-37, RN-42)
- `id_auditoria` PK, `id_usuario` FK, `fecha_hora`, `modulo`, `accion`, `valor_anterior`, `valor_nuevo`

**notificacion** (RN-40)
- `id_notificacion` PK, `id_usuario_destino` FK, `mensaje`, `fecha`, `leido` (bool)

**ranking_unidad** (RN-39)
- `id_ranking` PK, `id_unidad` FK, `periodo`, `puntaje`, `reglamento_aplicado` (referencia a configuración)

### 3.9 Relaciones clave (resumen)

- `club` 1─N `unidad`, `clase`, `poa`, `usuario`, `especialidad`
- `poa` 1─N `actividad_poa`; `actividad_poa` 1─N `cronograma` (RN-03)
- `clase` 1─N `sesion`, `miembro`, `especialidad_clase`
- `sesion` N─M `requisito` y N─M `especialidad`
- `miembro` 1─N `avance`, `asistencia`, `historial_academico`, `historial_unidad`
- `requisito` N─1 `categoria_requisito`, N─1 `version_cuadernillo`
- Toda tabla transaccional relevante alimenta `auditoria` mediante triggers o capa de servicio.

---

## 4. Escalabilidad

Aplicando RN-35 y RN-36 (reutilización para otros clubes y para el Club de Líderes):

1. **Multi-tenant lógico dentro del monolito**: todas las tablas de dominio (miembro, clase, especialidad, requisito, poa, etc.) incluyen `id_club` como clave de particionamiento lógico. Esto permite añadir nuevos clubes o el Club de Líderes sin cambios estructurales, solo nuevos registros de configuración.
2. **Configuración por club** (`club.configuracion` en JSON): reglamento de ranking (RN-39), plantillas de cronograma (RN-06), y parámetros de notificaciones (RN-40) quedan configurables sin tocar código.
3. **Separación en capas dentro del monolito**: API (controladores) → Servicios de dominio (POA, Avances, Especialidades, Auditoría, etc.) → Repositorios/ORM → Base de datos. Esto facilita, si en el futuro se requiere, extraer un módulo (p. ej. Reportes/Indicadores) como servicio independiente sin rediseñar el dominio.
4. **Índices y particionamiento físico**: índices por `id_club`, `id_clase`, `id_miembro` en las tablas de alto volumen (`avance`, `asistencia`, `auditoria`); particionamiento de `auditoria` por rango de fechas para no degradar el rendimiento con el crecimiento histórico (RN-23, RN-42).
5. **Colas/eventos internos para sincronización** (RN-03): la actualización del POA dispara un evento interno que recalcula los cronogramas afectados de forma asíncrona, evitando bloqueos cuando el volumen de clases/cronogramas crezca.
6. **Módulo de importación desacoplado** (RN-21): procesamiento por lotes (batch) del CSV del Sistema de Gestión de Clubes, para que la carga masiva no afecte el rendimiento transaccional del resto del sistema.
7. **Cacheo de indicadores y dashboard** (RN-34, RN-38): los indicadores se recalculan de forma programada (batch/cron) y se leen desde una tabla o vista materializada, en lugar de calcularse en cada request.
8. **Notificaciones extensibles** (RN-40): diseño basado en un proveedor de notificaciones (interno) que en versiones futuras se pueda intercambiar por correo electrónico o WhatsApp sin cambiar la lógica de negocio (patrón adaptador/interfaz).
9. **Versionado de cuadernillo sin migración de datos** (RN-27): al no reescribir avances existentes, las nuevas versiones se agregan como nuevos registros en `version_cuadernillo`/`requisito`, permitiendo crecer el catálogo sin operaciones costosas de migración.

---

## 5. Seguridad

1. **Modelo de roles y permisos (RBAC)** (RN-32, RN-33): control de acceso basado en `rol` + `permiso` por módulo y acción (crear/leer/editar/eliminar). Matriz base:

   | Rol | Alcance |
   |---|---|
   | Administrador | Control total del sistema |
   | Director / Secretario | Casi todos los módulos, alcance global del club |
   | Director Asociado | Solo los módulos que le sean asignados |
   | Instructor | Solo su(s) clase(s) asignada(s) — sesiones, avances, materiales |
   | Consejero | Solo su unidad — asistencia |
   | Conquistador | Solo su información personal y materiales autorizados |
   | Padre de familia | Solo el progreso de sus hijos |

2. **Restricción a nivel de fila (row-level security)** (RN-31, RN-45): las consultas de `sesion`, `avance` y `asistencia` deben filtrar obligatoriamente por `id_clase`/`id_unidad` asociada al usuario autenticado, para que un instructor no pueda ver ni modificar datos de clases ajenas.
3. **Autenticación y credenciales**: contraseñas con hash (bcrypt/argon2), tokens de sesión con expiración, y control de intentos fallidos.
4. **Auditoría obligatoria** (RN-26, RN-37, RN-42): toda operación de creación/edición/eliminación sobre POA, avances, materiales, unidades y usuarios queda registrada con usuario, fecha, hora, módulo, acción y valores anterior/nuevo. La auditoría es de solo lectura (append-only) para los roles distintos de Administrador.
5. **Datos sensibles minimizados** (RN-17, RN-18): el sistema no almacena información médica ni de póliza de seguro, solo estados administrativos, reduciendo la superficie de datos sensibles a proteger (alineado con minimización de datos).
6. **Integridad referencial y "soft delete"** (RN-20, RN-24): los miembros nunca se eliminan físicamente; se marcan como `Inactivo`, preservando la trazabilidad exigida y evitando pérdida accidental o maliciosa de historial.
7. **No duplicación en importación** (RN-21): el proceso de importación debe validar por un identificador único (p. ej. documento de identidad) antes de insertar, para evitar registros duplicados y posibles inconsistencias de permisos.
8. **Control de acceso a materiales** (RN-30): descarga/visualización de materiales validada según rol y relación con la clase/especialidad; los conquistadores y padres solo acceden a material "autorizado".
9. **Separación de ambientes y backups**: se recomienda ambiente de producción aislado, backups periódicos de la base de datos (especialmente `historial_academico`, `avance`, `auditoria`) y cifrado en tránsito (TLS) y en reposo para datos personales de menores de edad (conquistadores).
10. **Cumplimiento normativo de datos de menores** (RN-19): dado que gran parte de los miembros son menores de edad, el estado de "adhesión de padres" condiciona el uso de fotografías/material audiovisual; el sistema debe impedir la publicación de material asociado a un conquistador sin adhesión firmada.

---

*Documento generado a partir de las Reglas de Negocio RN-01 a RN-45 del Club de Conquistadores.*
