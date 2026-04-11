# Nomada Backend

Backend REST de la aplicacion Nomada, una plataforma orientada a buscar y organizar recursos de aprendizaje. La API esta desarrollada con Java y Spring Boot, usa MariaDB como base de datos, JWT para autenticacion y consume APIs publicas para devolver recursos externos de aprendizaje.

## Estado del backend

El backend cubre las funcionalidades principales definidas para el proyecto:

- Registro e inicio de sesion de usuarios.
- Autenticacion con JWT.
- Roles `USER` y `ADMIN`.
- Gestion de temas y recursos locales por administrador.
- Busqueda por termino con resultados separados en `recursosLocales`, `recursosExternos` y `aportaciones`.
- Integracion con APIs publicas para recursos externos.
- Aportaciones de usuarios asociadas a un tema o recurso.
- Favoritos de recursos locales, aportaciones y recursos externos.
- Historial de busquedas y consultas de recursos.
- Reporte y moderacion de aportaciones.

## Tecnologias

- Java 17 configurado en el proyecto.
- Spring Boot 4.
- Spring Web MVC.
- Spring Data JPA.
- Spring Security.
- MariaDB.
- Maven Wrapper.
- JWT implementado en el backend.

## Base de datos

La configuracion actual esta en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/nomada_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

Tablas principales:

- `usuarios`
- `temas`
- `recursos`
- `aportaciones`
- `favoritos`
- `historial`
- `reportes`

Tipos de recurso actuales:

```text
ARTICULO
VIDEO
PODCAST
LIBRO
```

## APIs publicas externas

El backend actua como intermediario. La app Android no llamara directamente a APIs externas; llamara a este backend y el backend normalizara los resultados al formato comun `RecursoDTO`.

Fuentes externas configuradas:

- Wikipedia MediaWiki API para `ARTICULO`.
- Open Library para `LIBRO`.
- iTunes Search API para `PODCAST`.
- YouTube Data API para `VIDEO`.

Configuracion:

```properties
external.apis.default-limit=5
external.apis.wikipedia.enabled=true
external.apis.open-library.enabled=true
external.apis.itunes.enabled=true
external.apis.youtube.enabled=true
external.apis.youtube.api-key=${YOUTUBE_API_KEY:}
```

YouTube requiere API key. Para usarlo en local:

```bash
export YOUTUBE_API_KEY="TU_API_KEY"
./mvnw spring-boot:run
```

Si no se configura `YOUTUBE_API_KEY`, la busqueda sigue funcionando con las demas APIs y recursos locales.

## Ejecutar el proyecto

1. Arrancar MariaDB.
2. Crear la base de datos:

```sql
CREATE DATABASE nomada_db;
```

3. Revisar credenciales en `application.properties`.
4. Ejecutar:

```bash
./mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

## Tests

Compilar sin tests:

```bash
./mvnw -DskipTests compile
```

Ejecutar tests unitarios principales:

```bash
./mvnw -Dtest=PasswordUtilsTest,TextValidationUtilsTest,JwtServiceTest,AuthenticatedUserTest,EstadoReporteTest,JsonTextUtilsTest,ExternalResourceSearchServiceTest,FavoritoServiceTest test
```

Probar arranque completo contra MariaDB local:

```bash
./mvnw -Dtest=NomadaV1ApplicationTests test
```

Ese ultimo test necesita que MariaDB este arrancado y que exista la base `nomada_db`.

## Autenticacion JWT

Flujo basico:

1. Registrar usuario con `POST /api/usuarios/registro`.
2. Hacer login con `POST /api/usuarios/login`.
3. Copiar el campo `token` de la respuesta.
4. En las peticiones protegidas, anadir el header:

```http
Authorization: Bearer TU_TOKEN
```

Ejemplo de login:

```http
POST /api/usuarios/login
Content-Type: application/json

{
  "email": "usuario@nomada.test",
  "password": "usuario123"
}
```

Respuesta esperada:

```json
{
  "token": "jwt...",
  "usuario": {
    "idUsuario": 2,
    "nombre": "Usuario Test",
    "email": "usuario@nomada.test",
    "rol": "USER"
  }
}
```

## Usuario administrador

El registro publico crea usuarios con rol `USER`. Para pruebas con administrador, se puede registrar un usuario y cambiar su rol a `ADMIN` directamente en la base de datos:

```sql
UPDATE usuarios
SET rol = 'ADMIN'
WHERE email = 'admin@nomada.test';
```

En las pruebas locales realizadas se uso:

```text
admin@nomada.test
admin123
```

Si la base se resetea, habra que volver a crear o convertir ese usuario.

## Flujo recomendado para Postman

1. Registrar un usuario normal.
2. Iniciar sesion con ese usuario y guardar el token `USER`.
3. Crear o convertir un usuario administrador.
4. Iniciar sesion con el administrador y guardar el token `ADMIN`.
5. Probar que `USER` no puede crear temas ni recursos.
6. Crear un tema con `ADMIN`.
7. Crear un recurso local con `ADMIN`.
8. Buscar un termino con `USER`.
9. Marcar como favorito un recurso local o externo con `USER`.
10. Consultar favoritos del usuario.
11. Consultar historial del usuario.
12. Crear una aportacion con un usuario.
13. Reportar esa aportacion con otro usuario.
14. Revisar y resolver el reporte con `ADMIN`.

La guia completa de endpoints esta en `docs/ENDPOINTS.md`.
