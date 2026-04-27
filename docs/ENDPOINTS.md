# Guia De Endpoints

Base URL local:

```text
http://localhost:8080
```

Header para endpoints protegidos:

```http
Authorization: Bearer TU_TOKEN
```

Tipos de recurso validos:

```text
ARTICULO
VIDEO
PODCAST
LIBRO
```

Roles:

- `USER`: usuario normal.
- `ADMIN`: administrador.

## Usuarios Y Autenticacion

### Registrar usuario

Publico.

```http
POST /api/usuarios/registro
Content-Type: application/json
```

Tambien existe:

```http
POST /api/usuarios
```

Body:

```json
{
  "nombre": "Usuario Test",
  "email": "usuario@nomada.test",
  "password": "usuario123"
}
```

Respuesta esperada:

```http
201 Created
```

```json
{
  "idUsuario": 2,
  "nombre": "Usuario Test",
  "email": "usuario@nomada.test",
  "password": null,
  "imagenPerfil": null,
  "rol": "USER",
  "fechaRegistro": "2026-04-11T20:37:23.072489"
}
```

Notas:

- El rol siempre se fuerza a `USER` en el registro publico.
- La contrasena debe tener minimo 6 caracteres.
- El email debe ser unico.

### Login

Publico.

```http
POST /api/usuarios/login
Content-Type: application/json
```

Body:

```json
{
  "email": "usuario@nomada.test",
  "password": "usuario123"
}
```

Respuesta esperada:

```http
200 OK
```

```json
{
  "token": "jwt...",
  "usuario": {
    "idUsuario": 2,
    "nombre": "Usuario Test",
    "email": "usuario@nomada.test",
    "password": null,
    "imagenPerfil": null,
    "rol": "USER",
    "fechaRegistro": "2026-04-11T20:37:23.072489"
  }
}
```

### Listar usuarios

Solo `ADMIN`.

```http
GET /api/usuarios
Authorization: Bearer ADMIN_TOKEN
```

Respuesta esperada:

```http
200 OK
```

### Obtener mi usuario autenticado

Usuario autenticado.

```http
GET /api/usuarios/me
Authorization: Bearer TOKEN
```

Uso recomendado:

- Rehidratar sesion al arrancar el frontend.
- Refrescar los datos del perfil tras login o edicion.

### Obtener usuario por id

Propietario o `ADMIN`.

```http
GET /api/usuarios/{id}
Authorization: Bearer TOKEN
```

Respuestas:

- `200 OK` si es el propio usuario o admin.
- `403 Forbidden` si un usuario intenta consultar otro perfil.
- `404 Not Found` si no existe.

### Actualizar usuario

Propietario o `ADMIN`.

```http
PUT /api/usuarios/{id}
Authorization: Bearer TOKEN
Content-Type: application/json
```

Body:

```json
{
  "nombre": "Usuario Actualizado",
  "email": "usuario@nomada.test",
  "password": "nueva123",
  "imagenPerfil": "https://example.com/avatar.png"
}
```

Notas:

- Si `password` va vacio o no se envia, no se cambia.
- Solo `ADMIN` puede cambiar `rol` mediante este endpoint.

### Eliminar usuario

Propietario o `ADMIN`.

```http
DELETE /api/usuarios/{id}
Authorization: Bearer TOKEN
```

Respuesta esperada:

```http
204 No Content
```

Notas:

- Si el usuario tiene aportaciones, favoritos, historial o reportes asociados, devuelve `400 Bad Request`.

## Temas

### Listar temas

Usuario autenticado.

```http
GET /api/temas
Authorization: Bearer TOKEN
```

### Obtener tema por id

Usuario autenticado.

```http
GET /api/temas/{id}
Authorization: Bearer TOKEN
```

### Crear tema

Solo `ADMIN`.

```http
POST /api/temas
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json
```

Body:

```json
{
  "nombre": "Historia",
  "descripcion": "Tema para recursos de historia"
}
```

Respuesta esperada:

```http
201 Created
```

### Actualizar tema

Solo `ADMIN`.

```http
PUT /api/temas/{id}
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json
```

Body:

```json
{
  "nombre": "Historia",
  "descripcion": "Descripcion actualizada"
}
```

### Eliminar tema

Solo `ADMIN`.

```http
DELETE /api/temas/{id}
Authorization: Bearer ADMIN_TOKEN
```

Notas:

- Si el tema tiene recursos o aportaciones asociados, devuelve `400 Bad Request`.

## Recursos

Los recursos locales los gestiona el administrador. Los recursos externos se devuelven en busquedas y solo se guardan en BBDD si el usuario los marca como favoritos.

### Listar recursos locales

Usuario autenticado.

```http
GET /api/recursos
Authorization: Bearer TOKEN
```

### Listar recursos por tema

Usuario autenticado.

```http
GET /api/recursos/tema/{idTema}
Authorization: Bearer TOKEN
```

### Obtener recurso por id

Usuario autenticado. Al consultar un recurso se registra historial de consulta.

```http
GET /api/recursos/{id}
Authorization: Bearer TOKEN
```

### Crear recurso local

Solo `ADMIN`.

```http
POST /api/recursos
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json
```

Body:

```json
{
  "titulo": "Guerra del Pacifico - recurso local",
  "urlEnlace": "https://example.com/guerra-pacifico",
  "descripcion": "Recurso local de prueba sobre la Guerra del Pacifico",
  "tipoRecurso": "ARTICULO",
  "fuente": "Recurso local",
  "fechaPublicacion": "2020-01-01",
  "idTema": 1
}
```

Campos obligatorios:

- `titulo`
- `urlEnlace`
- `tipoRecurso`
- `fuente`

### Actualizar recurso local

Solo `ADMIN`.

```http
PUT /api/recursos/{id}
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json
```

### Eliminar recurso local

Solo `ADMIN`.

```http
DELETE /api/recursos/{id}
Authorization: Bearer ADMIN_TOKEN
```

Notas:

- Si el recurso tiene aportaciones, favoritos o historial asociados, devuelve `400 Bad Request`.

## Busquedas

### Buscar por termino

Usuario autenticado.

```http
GET /api/busquedas?termino=guerra%20del%20pacifico
Authorization: Bearer TOKEN
```

Respuesta:

```json
{
  "termino": "guerra del pacifico",
  "recursosLocales": [],
  "recursosExternos": [],
  "aportaciones": []
}
```

Comportamiento:

- Busca recursos locales por titulo o descripcion.
- Consulta APIs externas configuradas.
- Busca aportaciones no eliminadas por contenido.
- Registra el termino en historial del usuario.

### Buscar filtrando por tipo

Usuario autenticado.

```http
GET /api/busquedas?termino=guerra%20del%20pacifico&tipos=LIBRO
Authorization: Bearer TOKEN
```

Varios tipos:

```http
GET /api/busquedas?termino=java&tipos=VIDEO,LIBRO
Authorization: Bearer TOKEN
```

Notas:

- Si `tipos` no se envia, busca todos los tipos.
- Si el tipo no existe, devuelve `400 Bad Request`.
- Los tipos validos son `ARTICULO`, `VIDEO`, `PODCAST`, `LIBRO`.

## Aportaciones

### Crear aportacion

Usuario autenticado.

```http
POST /api/aportaciones
Authorization: Bearer TOKEN
Content-Type: application/json
```

Body asociado a tema:

```json
{
  "contenido": "Aportacion de prueba sobre la Guerra del Pacifico",
  "idTema": 1
}
```

Body asociado a recurso:

```json
{
  "contenido": "Aportacion asociada a un recurso concreto",
  "idRecurso": 1
}
```

Notas:

- Debe estar asociada a `idTema` o `idRecurso`.
- No se permiten enlaces externos dentro del contenido.
- El backend asigna automaticamente el `idUsuario` desde el token.

### Listar aportaciones

Usuario autenticado.

```http
GET /api/aportaciones
Authorization: Bearer TOKEN
```

### Obtener aportacion por id

Usuario autenticado.

```http
GET /api/aportaciones/{id}
Authorization: Bearer TOKEN
```

### Listar aportaciones de un usuario

Propietario o `ADMIN`.

```http
GET /api/aportaciones/usuario/{idUsuario}
Authorization: Bearer TOKEN
```

### Listar aportaciones por tema

Usuario autenticado.

```http
GET /api/aportaciones/tema/{idTema}
Authorization: Bearer TOKEN
```

### Listar aportaciones por recurso

Usuario autenticado.

```http
GET /api/aportaciones/recurso/{idRecurso}
Authorization: Bearer TOKEN
```

### Listar aportaciones reportadas

Solo `ADMIN`.

```http
GET /api/aportaciones/reportadas
Authorization: Bearer ADMIN_TOKEN
```

### Actualizar aportacion

Usuario autenticado. La logica de servicio controla que se mantenga asociacion valida.

```http
PUT /api/aportaciones/{id}
Authorization: Bearer TOKEN
Content-Type: application/json
```

Body:

```json
{
  "contenido": "Contenido actualizado",
  "idTema": 1
}
```

### Eliminar aportacion

Propietario o `ADMIN`.

```http
DELETE /api/aportaciones/{id}
Authorization: Bearer TOKEN
```

La eliminacion es logica: la aportacion queda marcada como eliminada.

## Favoritos

### Crear favorito de recurso local

Usuario autenticado.

```http
POST /api/favoritos
Authorization: Bearer TOKEN
Content-Type: application/json
```

Body:

```json
{
  "idRecurso": 1
}
```

### Crear favorito de aportacion

Usuario autenticado.

```json
{
  "idAportacion": 1
}
```

### Crear favorito de recurso externo

Usuario autenticado.

Este caso se usa cuando el recurso viene de `recursosExternos` en una busqueda. El backend busca por `urlEnlace`; si ya existe lo reutiliza, y si no existe lo guarda como recurso local antes de crear el favorito.

```json
{
  "recursoExterno": {
    "titulo": "Guerra del Pacifico",
    "urlEnlace": "https://openlibrary.org/works/OL123",
    "descripcion": "Autor: Ejemplo",
    "tipoRecurso": "LIBRO",
    "fuente": "Open Library",
    "fechaPublicacion": "2000-01-01"
  }
}
```

Reglas:

- Hay que enviar exactamente uno de estos campos: `idRecurso`, `idAportacion` o `recursoExterno`.
- Si el favorito ya existe para ese usuario, devuelve `400 Bad Request`.
- No se puede guardar como favorita una aportacion eliminada.

### Listar favoritos de usuario

Propietario o `ADMIN`.

```http
GET /api/favoritos/usuario/{idUsuario}
Authorization: Bearer TOKEN
```

Respuesta esperada:

- Si el favorito es de recurso, el backend devuelve `idRecurso`, `tituloElemento` y tambien el objeto `recurso`.
- Si el favorito es de aportacion, devuelve `idAportacion`, `tituloElemento` y tambien el objeto `aportacion`.
- El campo `recursoExterno` se mantiene por compatibilidad y replica el mismo contenido de `recurso` cuando aplica.

### Eliminar favorito

Propietario o `ADMIN`.

```http
DELETE /api/favoritos/{id}
Authorization: Bearer TOKEN
```

Respuesta esperada:

```http
204 No Content
```

## Historial

El historial se crea automaticamente:

- Al realizar busquedas.
- Al consultar el detalle de un recurso local.

### Consultar historial de usuario

Propietario o `ADMIN`.

```http
GET /api/historial/usuario/{idUsuario}
Authorization: Bearer TOKEN
```

### Eliminar una entrada de historial

Propietario o `ADMIN`.

```http
DELETE /api/historial/{idHistorial}
Authorization: Bearer TOKEN
```

### Eliminar todo el historial de un usuario

Propietario o `ADMIN`.

```http
DELETE /api/historial/usuario/{idUsuario}
Authorization: Bearer TOKEN
```

## Reportes Y Moderacion

### Crear reporte

Usuario autenticado.

```http
POST /api/reportes
Authorization: Bearer TOKEN
Content-Type: application/json
```

Body:

```json
{
  "idAportacion": 1,
  "motivo": "Contenido inapropiado"
}
```

Reglas:

- Un usuario no puede reportar su propia aportacion.
- No se puede duplicar un reporte pendiente del mismo usuario sobre la misma aportacion.
- El reporte se crea con estado `PENDIENTE`.

### Consultar mis reportes

Usuario autenticado.

```http
GET /api/reportes/mis-reportes
Authorization: Bearer TOKEN
```

### Consultar reportes pendientes

Solo `ADMIN`.

```http
GET /api/reportes/pendientes
Authorization: Bearer ADMIN_TOKEN
```

### Consultar reporte por id

Propietario del reporte o `ADMIN`.

```http
GET /api/reportes/{idReporte}
Authorization: Bearer TOKEN
```

### Resolver reporte

Solo `ADMIN`.

```http
PUT /api/reportes/{idReporte}/resolver
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json
```

Mantener aportacion:

```json
{
  "accion": "MANTENER"
}
```

Eliminar aportacion:

```json
{
  "accion": "ELIMINAR"
}
```

Comportamiento:

- `MANTENER`: marca el reporte como revisado y conserva la aportacion.
- `ELIMINAR`: marca el reporte como revisado y elimina logicamente la aportacion.

## Codigos Habituales

- `200 OK`: operacion correcta con respuesta.
- `201 Created`: recurso creado.
- `204 No Content`: eliminacion correcta.
- `400 Bad Request`: datos invalidos o duplicados.
- `401 Unauthorized`: falta token o token invalido.
- `403 Forbidden`: token valido pero sin permisos.
- `404 Not Found`: recurso no encontrado.

## Secuencia Rapida Para Postman

1. `POST /api/usuarios/registro` para crear usuario normal.
2. `POST /api/usuarios/login` para obtener `USER_TOKEN`.
3. Crear o convertir admin en BBDD.
4. `POST /api/usuarios/login` con admin para obtener `ADMIN_TOKEN`.
5. `POST /api/temas` con `USER_TOKEN`: debe devolver `403`.
6. `POST /api/temas` con `ADMIN_TOKEN`: debe devolver `201`.
7. `POST /api/recursos` con `ADMIN_TOKEN`: debe devolver `201`.
8. `GET /api/busquedas?termino=guerra%20del%20pacifico` con `USER_TOKEN`: debe devolver bloques de busqueda.
9. `GET /api/busquedas?termino=guerra%20del%20pacifico&tipos=LIBRO` con `USER_TOKEN`: debe devolver solo libros en recursos filtrados.
10. `POST /api/favoritos` con `USER_TOKEN` usando `recursoExterno`: debe devolver `201`.
11. `GET /api/favoritos/usuario/{idUsuario}` con `USER_TOKEN`: debe devolver favoritos.
12. `GET /api/historial/usuario/{idUsuario}` con `USER_TOKEN`: debe devolver busquedas.
13. Crear segundo usuario, crear aportacion con el primero y reportarla con el segundo.
14. `GET /api/reportes/pendientes` con `ADMIN_TOKEN`: debe devolver reportes pendientes.
15. `PUT /api/reportes/{idReporte}/resolver` con `ADMIN_TOKEN`: debe resolver la moderacion.
