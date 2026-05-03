# Contrato Cerrado Frontend TFG

## Objetivo

Este documento cierra las decisiones necesarias para poder construir el frontend de Nomada sin abrir nuevas discusiones funcionales. El enfoque es deliberadamente simple: que el frontend quede bien presentado ante el tribunal, cubra todas las funcionalidades actuales del backend y no introduzca complejidad innecesaria.

No es una guia de produccion. Es el contrato operativo para implementar el frontend del TFG.

## Principios Cerrados

- El frontend sera una SPA conectada al backend REST actual.
- No se implementara refresh token.
- No se implementara paginacion server-side.
- No se implementaran notificaciones en tiempo real.
- No se implementaran subidas de archivos.
- No se consumiran APIs externas desde el frontend: todo pasa por el backend.
- El frontend debe priorizar claridad visual, buen acabado y flujos completos por encima de extras tecnicos.

## Base De Integracion

### Backend local

```text
http://localhost:8080
```

### CORS

El backend acepta peticiones cross-origin sobre `/api/**` para facilitar el desarrollo del frontend del TFG.

### Header de autenticacion

```http
Authorization: Bearer TU_TOKEN
```

## Estrategia De Sesion

La sesion del frontend queda cerrada asi:

- Guardar `token` y `usuario` en `localStorage`.
- Al arrancar la app:
  - si no hay token, ir a `login`
  - si hay token, llamar a `GET /api/usuarios/me`
- Si `GET /api/usuarios/me` responde `200`, actualizar el usuario local.
- Si cualquier endpoint responde `401`, borrar la sesion local y redirigir a `login`.
- El logout sera solo local: borrar `token` y `usuario`.

### Endpoint de rehidratacion

```http
GET /api/usuarios/me
Authorization: Bearer TOKEN
```

Respuesta:

```json
{
  "idUsuario": 2,
  "nombre": "Usuario Test",
  "email": "usuario@nomada.test",
  "imagenPerfil": null,
  "rol": "USER",
  "fechaRegistro": "2026-04-11T20:37:23.072489"
}
```

## Estrategia De Errores

Para este TFG, el frontend no dependera de bodies de error sofisticados. La regla cerrada es trabajar por codigo HTTP:

- `400 Bad Request`: mostrar mensaje funcional por pantalla o formulario.
- `401 Unauthorized`: cerrar sesion y enviar a `login`.
- `403 Forbidden`: mostrar mensaje de permisos insuficientes.
- `404 Not Found`: mostrar vista vacia o mensaje de elemento no encontrado.
- `500` o fallo de red: mostrar mensaje generico de error de conexion.

### Mensajes recomendados

- Login: `Credenciales incorrectas`.
- Registro o perfil: `Revisa los datos introducidos`.
- Creacion o edicion: `No se ha podido guardar`.
- Eliminacion: `No se ha podido completar la accion`.
- Reportes o favoritos duplicados: `La accion ya estaba realizada o los datos no son validos`.

## Decisiones De Datos

### Tipos de recurso

```text
ARTICULO
VIDEO
PODCAST
LIBRO
```

### Listados

Los listados del TFG quedan asi:

- Sin paginacion.
- Sin ordenacion server-side.
- Si hace falta buscar o filtrar en tablas pequenas, se hace en cliente.

Esto aplica a:

- usuarios
- temas
- recursos
- aportaciones
- favoritos
- historial
- reportes

## Contratos Que Debe Usar El Frontend

### Usuario

```json
{
  "idUsuario": 2,
  "nombre": "Usuario Test",
  "email": "usuario@nomada.test",
  "imagenPerfil": null,
  "rol": "USER",
  "fechaRegistro": "2026-04-11T20:37:23.072489"
}
```

Notas:

- `password` solo se envia en alta o actualizacion.
- Nunca se muestra en UI.

### Tema

```json
{
  "idTema": 1,
  "nombre": "Historia",
  "descripcion": "Tema para recursos de historia"
}
```

### Recurso

```json
{
  "idRecurso": 1,
  "titulo": "Guerra del Pacifico",
  "urlEnlace": "https://example.com",
  "descripcion": "Descripcion",
  "tipoRecurso": "ARTICULO",
  "fuente": "Wikipedia",
  "fechaPublicacion": "2020-01-01",
  "idTema": 1
}
```

### Resultado de busqueda

```json
{
  "termino": "guerra del pacifico",
  "recursosLocales": [],
  "recursosExternos": [],
  "aportaciones": []
}
```

Regla cerrada:

- El frontend debe pintar estos tres bloques por separado.
- Si uno viene vacio, se muestra estado vacio solo en ese bloque.

### Aportacion

```json
{
  "idAportacion": 1,
  "contenido": "Texto de aportacion",
  "fechaCreacion": "2026-04-11T20:37:23.072489",
  "idUsuario": 2,
  "idTema": 1,
  "idRecurso": null,
  "nombreUsuario": "Usuario Test",
  "reportada": false,
  "eliminada": false
}
```

### Favorito

```json
{
  "idFavorito": 1,
  "idUsuario": 2,
  "idRecurso": 4,
  "idAportacion": null,
  "fechaGuardado": "2026-04-11T20:37:23.072489",
  "tipoFavorito": "RECURSO",
  "tituloElemento": "Guerra del Pacifico",
  "recurso": {
    "idRecurso": 4,
    "titulo": "Guerra del Pacifico",
    "urlEnlace": "https://example.com",
    "descripcion": "Descripcion",
    "tipoRecurso": "LIBRO",
    "fuente": "Open Library",
    "fechaPublicacion": "2000-01-01",
    "idTema": null
  },
  "aportacion": null
}
```

Regla cerrada:

- Si `tipoFavorito === RECURSO`, la UI usa `favorito.recurso`.
- Si `tipoFavorito === APORTACION`, la UI usa `favorito.aportacion`.
- `tituloElemento` se puede usar para listados compactos.
- `recursoExterno` se considera legado y no hace falta usarlo en el frontend nuevo.

### Historial

```json
{
  "idHistorial": 1,
  "terminoBusqueda": "guerra del pacifico",
  "fecha": "2026-04-11T20:37:23.072489",
  "idUsuario": 2,
  "idRecurso": 1,
  "tituloRecurso": "Guerra del Pacifico"
}
```

Regla cerrada:

- Si `idRecurso` es `null`, la entrada representa una busqueda.
- Si `idRecurso` tiene valor, representa una consulta de detalle de recurso.

### Reporte

```json
{
  "idReporte": 1,
  "motivo": "Contenido inapropiado",
  "estado": "PENDIENTE",
  "fecha": "2026-04-11T20:37:23.072489",
  "idUsuarioReporta": 3,
  "idAportacion": 7,
  "nombreUsuarioReporta": "Usuario Moderador",
  "contenidoAportacion": "Texto reportado"
}
```

## Composicion De Pantallas

### Login

- `POST /api/usuarios/login`

### Registro

- `POST /api/usuarios/registro`

### Home autenticada

- mostrar resumen y acceso al buscador

### Busqueda

- `GET /api/busquedas?termino=...`
- `GET /api/busquedas?termino=...&tipos=VIDEO,LIBRO`

### Detalle de recurso

- `GET /api/recursos/{id}`
- `GET /api/aportaciones/recurso/{id}`

### Listado de temas

- `GET /api/temas`

### Detalle de tema

- `GET /api/temas/{id}`
- `GET /api/recursos/tema/{id}`
- `GET /api/aportaciones/tema/{id}`

### Favoritos

- `GET /api/favoritos/usuario/{idUsuario}`
- `DELETE /api/favoritos/{id}`

### Historial

- `GET /api/historial/usuario/{idUsuario}`
- `DELETE /api/historial/{idHistorial}`
- `DELETE /api/historial/usuario/{idUsuario}`

### Perfil

- `GET /api/usuarios/me`
- `PUT /api/usuarios/{id}`

### Mis aportaciones

- `GET /api/aportaciones/usuario/{idUsuario}`
- `POST /api/aportaciones`
- `PUT /api/aportaciones/{id}`
- `DELETE /api/aportaciones/{id}`

### Mis reportes

- `GET /api/reportes/mis-reportes`

### Admin usuarios

- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`

### Admin temas

- `GET /api/temas`
- `POST /api/temas`
- `PUT /api/temas/{id}`
- `DELETE /api/temas/{id}`

### Admin recursos

- `GET /api/recursos`
- `POST /api/recursos`
- `PUT /api/recursos/{id}`
- `DELETE /api/recursos/{id}`

### Admin moderacion

- `GET /api/aportaciones/reportadas`
- `GET /api/reportes/pendientes`
- `PUT /api/reportes/{idReporte}/resolver`

## Reglas De UI Cerradas

- Si un usuario no es `ADMIN`, no se renderizan rutas ni menus admin.
- El buscador es el centro del producto y debe estar visible o accesible en un clic.
- El detalle de recurso debe permitir ver aportaciones y crear aportacion.
- Los estados vacios deben estar cuidados visualmente.
- Los formularios deben validar en cliente lo basico antes de enviar:
  - email valido
  - password minimo 6 caracteres
  - termino de busqueda minimo 2 caracteres
  - aportacion no vacia
- El frontend puede ser bonito, pero sin animaciones o patrones que compliquen la demo.

## Fuera De Alcance

Queda explicitamente fuera del frontend del TFG:

- refresh token
- recuperacion de contraseña
- internacionalizacion
- modo offline
- websocket
- paginacion infinita
- panel de analitica
- subida real de avatar

## Conclusion Operativa

Con este contrato, el frontend puede implementarse completo usando solo la API actual y las decisiones aqui fijadas. Si durante el desarrollo surge una duda no contemplada, se resuelve siempre con el criterio mas simple y visualmente limpio, sin ampliar alcance funcional.
