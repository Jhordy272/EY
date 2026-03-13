# Solución – API REST Registro de Usuarios

## Stack tecnológico

| Componente       | Tecnología                          |
|------------------|-------------------------------------|
| Lenguaje         | Java 21                             |
| Framework        | Spring Boot 4.0.3                   |
| Persistencia     | Spring Data JPA + Hibernate         |
| Base de datos    | H2 (in-memory)                      |
| Token            | JWT (nimbus-jose-jwt / HS256)       |
| Build            | Gradle (Kotlin DSL)                 |
| Servidor         | Tomcat Embedded                     |
| Documentación    | SpringDoc OpenAPI (Swagger UI)      |
| Utilidades       | Lombok                              |
| Testing          | JUnit 5 + Mockito                   |

---

## Arquitectura

La solución sigue una arquitectura MVC en capas:

```
Controller  →  Service  →  Repository  →  H2 (in-memory)
```

| Capa           | Clase                | Responsabilidad                                      |
|----------------|----------------------|------------------------------------------------------|
| Controller     | `UserController`     | Recibe el request HTTP, valida el body, retorna response |
| Service        | `UserService`        | Lógica de negocio: validar email, generar token, persistir |
| Repository     | `UserRepository`     | Acceso a la tabla `users`                            |
| Repository     | `PhoneRepository`    | Acceso a la tabla `phones`                           |
| Config/Security | `JwtService`        | Generación y validación de tokens JWT (HS256)        |

---

## Estructura del proyecto

```
src/main/java/com/ey/EY/
├── config/
│   ├── doc/
│   │   └── SwaggerConfig.java            # Configuración OpenAPI
│   └── security/
│       └── JwtService.java               # Generación y validación JWT
├── controller/
│   └── UserController.java               # POST /api/users
├── dto/
│   ├── MessageResponse.java              # { "mensaje": "..." }
│   ├── PhoneRequest.java
│   ├── PhoneResponse.java
│   ├── UserRequest.java                  # Input con validaciones
│   └── UserResponse.java                 # Output con datos del usuario
├── exception/
│   ├── EmailAlreadyExistsException.java
│   ├── GlobalExceptionHandler.java       # @RestControllerAdvice
│   └── TokenGenerationException.java
├── model/
│   ├── Phone.java                        # Entidad @Entity tabla phones
│   └── User.java                         # Entidad @Entity tabla users
├── repository/
│   ├── PhoneRepository.java
│   └── UserRepository.java
├── service/
│   └── UserService.java                  # Lógica de registro
└── EyApplication.java
```

---

## Modelo de datos

### Tabla `users`

| Columna      | Tipo           | Restricción              |
|--------------|----------------|--------------------------|
| id           | UUID           | PK, NOT NULL             |
| name         | VARCHAR(255)   | NOT NULL                 |
| email        | VARCHAR(255)   | NOT NULL, UNIQUE         |
| password     | VARCHAR(255)   | NOT NULL                 |
| token        | VARCHAR(512)   | NOT NULL                 |
| isactive     | BOOLEAN        | NOT NULL                 |
| created      | TIMESTAMP      | NOT NULL                 |
| modified     | TIMESTAMP      | NOT NULL                 |
| last_login   | TIMESTAMP      | NOT NULL                 |

### Tabla `phones`

| Columna      | Tipo           | Restricción              |
|--------------|----------------|--------------------------|
| id           | UUID           | PK, NOT NULL             |
| number       | VARCHAR(255)   | NOT NULL                 |
| citycode     | VARCHAR(255)   | NOT NULL                 |
| contrycode   | VARCHAR(255)   | NOT NULL                 |
| user_id      | UUID           | NOT NULL, FK → users(id) |

---

## Endpoint

### `POST /api/users`

**Request:**

```json
{
  "name": "Juan Rodriguez",
  "email": "juan@dominio.cl",
  "password": "Hunter12",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ]
}
```

**Validaciones del usuario:**

| Campo      | Regla                                                   |
|------------|---------------------------------------------------------|
| name       | Requerido                                               |
| email      | Requerido, debe ser `@dominio.cl`                       |
| password   | Requerido, al menos 1 mayúscula, minúsculas y 2 números |
| phones     | Al menos un teléfono requerido                          |

**Validaciones del teléfono:**

| Campo      | Regla      |
|------------|------------|
| number     | Requerido  |
| citycode   | Requerido  |
| contrycode | Requerido  |

**Response exitoso `201 Created`:**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Juan Rodriguez",
  "email": "juan@dominio.cl",
  "phones": [
    {
      "number": "1234567",
      "citycode": "1",
      "contrycode": "57"
    }
  ],
  "created": "2026-03-13T10:30:00",
  "modified": "2026-03-13T10:30:00",
  "last_login": "2026-03-13T10:30:00",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "isactive": true
}
```

**Respuestas de error:**

| Código | Causa | Mensaje |
|--------|-------|---------|
| 400 | Nombre vacío | `El nombre es requerido` |
| 400 | Correo vacío | `El correo es requerido` |
| 400 | Correo dominio inválido | `El correo debe pertenecer al dominio dominio.cl` |
| 400 | Contraseña vacía | `La contraseña es requerida` |
| 400 | Contraseña débil | `La contraseña debe tener al menos una mayúscula, letras minúsculas y dos números` |
| 400 | Sin teléfonos | `Debe incluir al menos un teléfono` |
| 400 | Número de teléfono vacío | `El número es requerido` |
| 400 | Código de ciudad vacío | `El código de ciudad es requerido` |
| 400 | Código de país vacío | `El código de país es requerido` |
| 409 | Email ya registrado | `El correo ya registrado` |
| 500 | Error generando token | `Error al generar el token de acceso` |

---

## Flujo de registro

1. El cliente envía `POST /api/users` con el body JSON
2. `UserController` valida el body con `@Valid`
3. `UserService` verifica si el email ya existe → lanza `EmailAlreadyExistsException` si aplica
4. `JwtService` genera un token JWT firmado con HS256
5. Se persiste el `User` en la tabla `users` con `BCrypt` en la contraseña
6. Se persisten los `Phone` en la tabla `phones` con FK al usuario
7. Se retorna el `UserResponse` con todos los datos del usuario creado

## Documentación interactiva (Swagger UI)

La API cuenta con documentación interactiva generada automáticamente con **SpringDoc OpenAPI**.

**URL:** `http://localhost:8080/swagger-ui/index.html`

Desde Swagger UI puedes:
- Ver el endpoint disponible con su descripción
- Revisar el schema del request con valores de ejemplo precargados
- Ejecutar el endpoint directamente desde el navegador
- Ver los posibles códigos de respuesta (`201`, `400`, `409`, `500`) con ejemplos de cada uno

| Sección         | Detalle                                    |
|-----------------|--------------------------------------------|
| Tag             | `Usuarios`                                 |
| Endpoint        | `POST /api/users`                          |
| Descripción     | Crea un nuevo usuario y retorna su información con token de acceso |

---

## Tests

```bash
./gradlew test
```

| Clase                  | Tests | Cubre                                         |
|------------------------|-------|-----------------------------------------------|
| `UserServiceTest`      | 5     | Registro exitoso, email duplicado, token fallido, phones |
| `UserControllerTest`   | 8     | HTTP 201, 400, 409 y validaciones de input    |
| `EyApplicationTests`   | 1     | Carga del contexto de Spring                  |

**Total: 14 tests**
