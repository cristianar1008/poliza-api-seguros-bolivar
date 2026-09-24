# API de Gestion de Polizas (Prueba Tecnica - Modulo 2)

API REST para el manejo de polizas de arrendamiento de inmuebles (Individual y Colectiva),
sus riesgos asociados, renovaciones y notificacion de eventos a un CORE externo (mockeado).

## Stack

- Java 21
- Spring Boot 4.1.1 (spring-boot-starter-webmvc, spring-boot-starter-data-jpa, spring-boot-starter-validation)
- PostgreSQL 16 (via Docker Compose)
- Maven

## Como levantar el proyecto

### 1. Base de datos

Con Docker Desktop corriendo, desde la raiz del proyecto:

```
docker compose up -d
```

Esto levanta un contenedor Postgres (`poliza-api-db`) con la base `polizasdb`, usuario
`poliza_user` / password `poliza_pass`, expuesta en `localhost:5432`.

Si necesitas reiniciar el esquema desde cero (por ejemplo tras cambiar entidades):

```
docker compose down -v
docker compose up -d
```

### 2. Aplicacion

Desde IntelliJ (Run `PolizaApiSegurosBolivarApplication`) o por linea de comandos:

```
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Documentación interactiva (Swagger)

Con la app corriendo, la documentación OpenAPI está disponible en:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- JSON crudo: `http://localhost:8080/v3/api-docs`

Swagger UI carga sin necesitar el header (para que la documentación sea navegable
libremente), pero para probar los endpoints reales con "Try it out" hay que hacer clic
en **Authorize** e ingresar `123456` — el esquema de seguridad ya está configurado para
enviarlo en el header `x-api-key` en cada llamada.

Al arrancar, un `CommandLineRunner` (`TipoDocumentoSeeder`) inserta automaticamente el
catalogo base de tipos de documento: `CC`, `CE`, `NIT`, `PAS` (solo si la tabla esta vacia).

Además, si las tablas de negocio están vacías, otro `CommandLineRunner`
(`DemoDataSeeder`, corre después del anterior) carga datos de ejemplo para no
empezar de cero: una póliza INDIVIDUAL (con su renovación ya aplicada, para
poder probar `/polizas/{id}/renovaciones` de una vez) y una póliza COLECTIVA
con dos riesgos, cada una con sus terceros (tomador, asegurado, beneficiario)
y sus eventos de notificación. Es idempotente: si ya hay pólizas creadas
(datos reales o de un arranque anterior), no vuelve a sembrar nada.

## Seguridad

Todos los endpoints, excepto `/core-mock/**`, exigen el header:

```
x-api-key: 123456
```

Una peticion sin el header, o con un valor distinto, responde `401 Unauthorized`.

## Modelo de datos (resumen)

- **Poliza**: tipo (INDIVIDUAL/COLECTIVA), estado (ACTIVA/RENOVADA/CANCELADA), fechas de
  vigencia, valor de canon y prima, y su `tomador` (un `Tercero`).
- **Riesgo**: pertenece a una poliza; tiene su propio `asegurado` y `beneficiario`
  (ambos `Tercero`), y estado (ACTIVO/CANCELADO).
- **Tercero**: persona natural o juridica identificada por tipo+numero de documento;
  se reutiliza como tomador de una poliza o como asegurado/beneficiario de un riesgo.
- **TipoDocumento**: catalogo (CC, CE, NIT, PAS) referenciado por Tercero.
- **RenovacionPoliza**: historial de cada renovacion (canon/prima antes y despues, IPC aplicado).
- **EventoNotificacion**: outbox de eventos de notificacion (CREACION/RENOVACION) generados
  al crear o renovar una poliza; un publicador `@Scheduled` los marca como enviados.

## Reglas de negocio

- Una poliza **INDIVIDUAL** solo puede tener 1 riesgo (se crea junto con la poliza).
- Solo una poliza **COLECTIVA** admite agregar riesgos adicionales via `POST /polizas/{id}/riesgos`.
- No se puede renovar una poliza que ya esta **CANCELADA**.
- Cancelar una poliza cancela automaticamente todos sus riesgos.
- Al renovar, el canon y la prima se incrementan en `+IPC` (configurable en
  `poliza.renovacion.ipc`, por defecto 6.5%) y el estado pasa a **RENOVADA**.

## Endpoints

Todos los ejemplos asumen `api-key` correcta en el header `x-api-key: 123456`.

### Crear poliza INDIVIDUAL

```
POST /polizas
Content-Type: application/json
x-api-key: 123456

{
  "tipo": "INDIVIDUAL",
  "fechaInicio": "2026-01-01",
  "fechaFin": "2026-12-31",
  "valorCanon": 1500000,
  "tomador": {
    "tipoDocumentoCodigo": "CC",
    "numeroDocumento": "1000111222",
    "nombre": "Juan Perez",
    "tipoPersona": "NATURAL",
    "correo": "juan@example.com",
    "telefono": "3001234567"
  },
  "asegurado": {
    "tipoDocumentoCodigo": "CC",
    "numeroDocumento": "1000111222",
    "nombre": "Juan Perez",
    "tipoPersona": "NATURAL"
  },
  "beneficiario": {
    "tipoDocumentoCodigo": "NIT",
    "numeroDocumento": "900123456",
    "nombre": "Inmobiliaria XYZ SAS",
    "tipoPersona": "JURIDICA"
  },
  "riesgoDescripcion": "Apartamento 501, Edificio Central, Bogota"
}
```

Para una poliza **COLECTIVA**, `tomador` es obligatorio pero `asegurado`, `beneficiario` y
`riesgoDescripcion` se omiten (los riesgos se agregan despues con `POST /polizas/{id}/riesgos`).

### Listar polizas (filtrable)

```
GET /polizas
GET /polizas?tipo=INDIVIDUAL
GET /polizas?estado=ACTIVA
GET /polizas?tipo=COLECTIVA&estado=ACTIVA
```

### Consultar una poliza / sus riesgos

```
GET /polizas/{id}
GET /polizas/{id}/riesgos
```

### Renovar una poliza

```
POST /polizas/{id}/renovar
```

Incrementa canon y prima en `+IPC`, recalcula el nuevo periodo de vigencia, cambia el
estado a `RENOVADA`, guarda un registro en el historial de renovaciones y notifica al
CORE (mock) y al outbox de notificaciones.

### Cancelar una poliza

```
POST /polizas/{id}/cancelar
```

Cambia el estado a `CANCELADA` y cancela en cascada todos sus riesgos.

### Agregar riesgo (solo COLECTIVA)

```
POST /polizas/{id}/riesgos
Content-Type: application/json
x-api-key: 123456

{
  "descripcion": "Local comercial 12, Centro Comercial Plaza",
  "asegurado": {
    "tipoDocumentoCodigo": "CC",
    "numeroDocumento": "1000222333",
    "nombre": "Maria Gomez",
    "tipoPersona": "NATURAL"
  },
  "beneficiario": {
    "tipoDocumentoCodigo": "NIT",
    "numeroDocumento": "900123456",
    "nombre": "Inmobiliaria XYZ SAS",
    "tipoPersona": "JURIDICA"
  }
}
```

Si la poliza es INDIVIDUAL, responde `409 Conflict`.

### Cancelar un riesgo puntual

```
POST /riesgos/{id}/cancelar
```

### Historial de renovaciones y notificaciones de una poliza

```
GET /polizas/{id}/renovaciones
GET /polizas/{id}/notificaciones
```

### Catalogo de tipos de documento

```
GET /tipos-documento
```

### Mock del CORE

Endpoint publico (no requiere `x-api-key`), invocado internamente por el adapter de
integracion con el CORE cada vez que se crea, renueva o actualiza una poliza. Su unico
proposito es registrar en logs que la operacion se intento enviar:

```
POST /core-mock/evento
Content-Type: application/json

{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

## Manejo de errores

- `400 Bad Request`: validaciones de campos (`@Valid`) o argumentos invalidos.
- `401 Unauthorized`: header `x-api-key` ausente o incorrecto.
- `404 Not Found`: poliza o riesgo inexistente.
- `409 Conflict`: violacion de una regla de negocio (renovar poliza cancelada, agregar
  riesgo a una poliza INDIVIDUAL, etc.).

## Notas de diseño

Este proyecto es el "Modulo 2" (implementacion) de la prueba, construido sobre las
decisiones de arquitectura y modelo de datos documentadas en el "Modulo 1". Por alcance
de la prueba, el Circuit Breaker para la integracion con el CORE y el uso de un broker de
mensajeria real (Kafka/RabbitMQ) para el outbox de notificaciones quedan documentados como
arquitectura objetivo en el Modulo 1, pero no se implementan aqui: el mock del CORE se
llama de forma sincrona via `RestClient`, y el outbox de notificaciones se procesa con un
publicador `@Scheduled` en el mismo proceso.
