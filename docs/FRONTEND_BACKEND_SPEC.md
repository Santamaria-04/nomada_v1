# Especificacion Frontend Sobre El Backend De Nomada

## Objetivo

Este documento resume, con enfoque de frontend, todo lo que ya soporta el backend de Nomada y como conviene explotarlo en la aplicacion cliente. La idea es simple: si el backend ya lo hace, el frontend debe diseñarse para usarlo. Aqui se recoge el alcance funcional real, los endpoints disponibles, los contratos de datos, los permisos, los flujos de usuario, los estados de interfaz y las limitaciones actuales.

No es una propuesta teorica. Esta basado en el comportamiento real del backend actual.

Para la integracion final del frontend del TFG, las decisiones cerradas estan en [FRONTEND_TFG_CONTRACT.md](/Users/pablosantamariagonzalez/Desktop/ESIC/TFG/codigo/back/nomada_v1/docs/FRONTEND_TFG_CONTRACT.md).

## Resumen Ejecutivo

El backend ya permite montar una plataforma bastante completa de aprendizaje con estas capacidades:

- Registro e inicio de sesion.
- Autenticacion con JWT.
- Roles `USER` y `ADMIN`.
- Perfil de usuario editable.
- Catalogo de temas.
- Catalogo de recursos locales gestionados por admin.
- Busqueda unificada con resultados locales, externos y aportaciones.
- Integracion con APIs externas normalizadas por backend.
- Favoritos de recursos locales, recursos externos y aportaciones.
- Historial automatico de busquedas y consultas de recursos.
- Aportaciones de usuarios ligadas a temas o recursos.
- Reporte de aportaciones.
- Moderacion administrativa de reportes.

Con esto ya se puede construir:

- Zona publica minima de autenticacion.
- Zona privada principal para usuarios.
- Buscador global potente.
- Detalles de recursos.
- Vista de temas.
- Perfil de usuario.
- Favoritos.
- Historial.
- Modulo social de aportaciones.
- Moderacion admin.
- Panel de administracion de temas, recursos y usuarios.

## Base Tecnica

### Base URL local

```text
http://localhost:8080
```

### Header de autenticacion

```http
Authorization: Bearer TU_TOKEN
```

### Roles

- `USER`: usuario normal.
- `ADMIN`: administrador.

### Tipos de recurso soportados

```text
ARTICULO
VIDEO
PODCAST
LIBRO
```

## Vision General Del Producto

La aplicacion se puede entender en cinco bloques:

1. Identidad y acceso.
2. Descubrimiento de contenido.
3. Organizacion personal del contenido.
4. Capa social basada en aportaciones.
5. Gestion y moderacion administrativa.

### Identidad y acceso

El usuario se registra, inicia sesion y trabaja siempre autenticado para cualquier operacion salvo registro y login.

### Descubrimiento de contenido

El usuario busca un termino y el backend devuelve:

- Recursos locales propios de la plataforma.
- Recursos externos agregados desde APIs publicas.
- Aportaciones de usuarios relacionadas con la busqueda.

### Organizacion personal

El usuario puede:

- Guardar favoritos.
- Revisar historial.
- Editar perfil.

### Capa social

El usuario puede:

- Publicar aportaciones.
- Editar sus aportaciones.
- Eliminar logicamente sus aportaciones.
- Reportar aportaciones de otros usuarios.

### Administracion

El admin puede:

- Gestionar usuarios.
- Gestionar temas.
- Gestionar recursos locales.
- Revisar aportaciones reportadas.
- Resolver reportes.

## Modulo De Autenticacion Y Sesion

### Registro

Endpoints:

- `POST /api/usuarios/registro`
- `POST /api/usuarios`

Body esperado:

```json
{
  "nombre": "Usuario Test",
  "email": "usuario@nomada.test",
  "password": "usuario123"
}
```

Comportamiento:

- El backend fuerza el rol a `USER`.
- El email se normaliza a minusculas.
- La password se hashea en backend.
- `imagenPerfil` es opcional.
- `fechaRegistro` la genera el backend.

Validaciones:

- `nombre` obligatorio.
- `email` obligatorio.
- `email` con formato valido.
- `email` unico.
- `password` obligatoria y minimo 6 caracteres.

Respuesta:

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

Implicacion para frontend:

- Pantalla de registro simple.
- No mostrar selector de rol en registro.
- Tras alta, se puede redirigir a login o logar automaticamente solo si el front lo decide, porque el backend no devuelve token en registro.

### Login

Endpoint:

- `POST /api/usuarios/login`

Body:

```json
{
  "email": "usuario@nomada.test",
  "password": "usuario123"
}
```

Respuesta:

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

Comportamiento:

- Si credenciales invalidas, devuelve `401 Unauthorized`.
- El token expira en 24 horas.
- No existe refresh token.

Implicacion para frontend:

- Guardar `token` y `usuario`.
- Aplicar guards por autenticacion y por rol.
- Preparar cierre de sesion manual al expirar token.
- Si cualquier endpoint devuelve `401`, borrar sesion local y redirigir a login.

### Estado de sesion recomendado en frontend

El estado de autenticacion deberia guardar como minimo:

- `token`
- `usuario.idUsuario`
- `usuario.nombre`
- `usuario.email`
- `usuario.imagenPerfil`
- `usuario.rol`
- `usuario.fechaRegistro`

No conviene guardar `password`.

## Modulo De Usuario Y Perfil

### Listado de usuarios

Endpoint:

- `GET /api/usuarios`

Permisos:

- Solo `ADMIN`.

Uso frontend:

- Tabla de usuarios en panel admin.
- Filtros cliente por nombre, email o rol si hace falta.

### Detalle de usuario

Endpoint:

- `GET /api/usuarios/{id}`

Permisos:

- El propio usuario.
- Un admin.

Uso frontend:

- Pantalla de perfil propio.
- Vista de detalle de usuario en admin.

### Actualizacion de usuario

Endpoint:

- `PUT /api/usuarios/{id}`

Permisos:

- El propio usuario.
- Un admin.

Body posible:

```json
{
  "nombre": "Usuario Actualizado",
  "email": "usuario@nomada.test",
  "password": "nueva123",
  "imagenPerfil": "https://example.com/avatar.png",
  "rol": "ADMIN"
}
```

Reglas:

- Si `password` no se manda o va vacia, no cambia.
- Solo admin puede cambiar `rol`.
- `imagenPerfil` es un string opcional, no existe subida de archivo.

Uso frontend:

- Formulario de perfil propio.
- Formulario admin para editar usuario.
- Switch o select de rol solo visible para admins.

### Eliminacion de usuario

Endpoint:

- `DELETE /api/usuarios/{id}`

Permisos:

- El propio usuario.
- Un admin.

Uso frontend:

- Borrar cuenta propia.
- Eliminar usuario desde admin.

Recomendacion de UX:

- Confirmacion modal fuerte.
- Si el usuario se elimina a si mismo, limpiar sesion y redirigir.

## Modulo De Temas

### Listado y detalle

Endpoints:

- `GET /api/temas`
- `GET /api/temas/{id}`

Permisos:

- Cualquier usuario autenticado.

Campos:

- `idTema`
- `nombre`
- `descripcion`

Uso frontend:

- Listado de categorias.
- Filtros de navegacion.
- Selectores en formularios de recursos.
- Contexto de aportaciones por tema.

### Creacion, edicion y borrado

Endpoints:

- `POST /api/temas`
- `PUT /api/temas/{id}`
- `DELETE /api/temas/{id}`

Permisos:

- Solo `ADMIN`.

Body de alta/edicion:

```json
{
  "nombre": "Historia",
  "descripcion": "Tema para recursos de historia"
}
```

Validacion:

- `nombre` obligatorio.

Uso frontend:

- CRUD de temas en admin.
- Modal o formulario dedicado de mantenimiento.

## Modulo De Recursos Locales

### Que es un recurso local

Es un recurso guardado y gestionado dentro del sistema. Lo crea el admin o se puede llegar a persistir indirectamente cuando un usuario favorita un recurso externo.

### Estructura real de recurso

```json
{
  "idRecurso": 1,
  "titulo": "Guerra del Pacifico - recurso local",
  "urlEnlace": "https://example.com/guerra-pacifico",
  "descripcion": "Recurso local de prueba",
  "tipoRecurso": "ARTICULO",
  "fuente": "Recurso local",
  "fechaPublicacion": "2020-01-01",
  "idTema": 1
}
```

### Listado

Endpoint:

- `GET /api/recursos`

Permisos:

- Cualquier usuario autenticado.

Uso frontend:

- Biblioteca local.
- Catologo admin.
- Fuente de cards reutilizable.

### Detalle de recurso

Endpoint:

- `GET /api/recursos/{id}`

Permisos:

- Cualquier usuario autenticado.

Comportamiento relevante:

- Si el usuario esta autenticado, se registra automaticamente en historial como consulta de recurso.

Uso frontend:

- Pagina de detalle.
- Vista de recurso desde favorito o historial.
- Entrada al flujo de aportaciones asociadas a ese recurso.

### Alta de recurso

Endpoint:

- `POST /api/recursos`

Permisos:

- Solo `ADMIN`.

### Edicion de recurso

Endpoint:

- `PUT /api/recursos/{id}`

Permisos:

- Solo `ADMIN`.

### Borrado de recurso

Endpoint:

- `DELETE /api/recursos/{id}`

Permisos:

- Solo `ADMIN`.

### Validaciones reales

- `titulo` obligatorio.
- `urlEnlace` obligatoria.
- `tipoRecurso` obligatorio.
- `fuente` obligatoria.
- `fechaPublicacion` no puede ser futura.
- `idTema` puede ir vacio o apuntar a un tema existente.

### Tipos de recurso y visualizacion sugerida

- `ARTICULO`: iconografia de lectura o documento.
- `VIDEO`: miniatura o iconografia de play.
- `PODCAST`: iconografia de audio.
- `LIBRO`: iconografia de libro.

## Modulo De Busqueda Global

### Endpoint principal

- `GET /api/busquedas?termino=java`
- `GET /api/busquedas?termino=java&tipos=VIDEO,LIBRO`

Permisos:

- Cualquier usuario autenticado.

### Respuesta

```json
{
  "termino": "java",
  "recursosLocales": [],
  "recursosExternos": [],
  "aportaciones": []
}
```

### Que hace realmente la busqueda

- Busca recursos locales por `titulo` o `descripcion`.
- Consulta proveedores externos configurados.
- Busca aportaciones no eliminadas por `contenido`.
- Registra el termino en historial.

### Validaciones

- `termino` debe tener al menos 2 caracteres.
- `tipos` es opcional.
- Si `tipos` contiene un valor no valido, devuelve `400`.

### Tipos admitidos en filtro

- `ARTICULO`
- `VIDEO`
- `PODCAST`
- `LIBRO`

### Uso frontend recomendado

La pantalla de resultados deberia separar claramente:

- Bloque de recursos locales.
- Bloque de recursos externos.
- Bloque de aportaciones.

Conviene que el front permita:

- Filtro por tipo.
- Reintentar busqueda.
- Estados vacios por bloque.
- Guardar un recurso externo como favorito.
- Navegar a detalle de recurso local.
- Navegar a aportaciones asociadas.

### Consideraciones UX

- El historial se genera solo con buscar; no hace falta endpoint adicional.
- La busqueda ya viene agregada por backend; no es necesario lanzar multiples requests por seccion.
- No existe paginacion ni scroll server side.

## Modulo De Recursos Externos

### Que es un recurso externo en este backend

Es un resultado traido de una API externa y adaptado a `RecursoDTO`. El frontend nunca llama directamente a Wikipedia, Open Library, Apple Podcasts o YouTube; todo llega desde backend.

### Fuentes integradas

- Wikipedia para `ARTICULO`.
- Open Library para `LIBRO`.
- Apple Podcasts para `PODCAST`.
- YouTube para `VIDEO`.

### Formato devuelto

Todos los recursos externos se adaptan al mismo contrato:

```json
{
  "idRecurso": null,
  "titulo": "Titulo externo",
  "urlEnlace": "https://...",
  "descripcion": "Descripcion o snippet",
  "tipoRecurso": "VIDEO",
  "fuente": "YouTube",
  "fechaPublicacion": "2024-01-10",
  "idTema": null
}
```

### Consideraciones importantes para frontend

- Los recursos externos no tienen por que traer `idRecurso`.
- Si el usuario los guarda en favoritos, el backend puede persistirlos internamente.
- El front puede renderizarlos con el mismo componente base que los recursos locales.
- Conviene marcar visualmente su origen con `fuente`.

### Disponibilidad por proveedor

- Wikipedia, Open Library y Apple Podcasts funcionan si estan habilitados.
- YouTube solo devuelve resultados si existe API key configurada en backend.
- Si un proveedor falla, la busqueda no cae; simplemente puede venir menos contenido externo.

## Modulo De Aportaciones

### Que representa una aportacion

Es contenido textual generado por usuarios, asociado como minimo a un tema o a un recurso.

### Contrato real

```json
{
  "idAportacion": 1,
  "contenido": "Aportacion de prueba sobre la Guerra del Pacifico",
  "fechaCreacion": "2026-04-11T21:00:00",
  "idUsuario": 2,
  "idTema": 1,
  "idRecurso": null,
  "nombreUsuario": "Usuario Test",
  "reportada": false,
  "eliminada": false
}
```

### Crear aportacion

Endpoint:

- `POST /api/aportaciones`

Permisos:

- Cualquier usuario autenticado.

Bodies validos:

```json
{
  "contenido": "Aportacion de prueba sobre la Guerra del Pacifico",
  "idTema": 1
}
```

```json
{
  "contenido": "Aportacion asociada a un recurso concreto",
  "idRecurso": 1
}
```

Comportamiento:

- El backend asigna `idUsuario` desde el token.
- El backend fija `fechaCreacion`.
- La aportacion nace con `reportada = false`.
- La aportacion nace con `eliminada = false`.

Validaciones:

- `contenido` obligatorio.
- Debe existir `idTema` o `idRecurso`.
- No se permiten enlaces externos dentro de `contenido`.

Nota importante:

- La validacion exige al menos `idTema` o `idRecurso`, no exactamente uno. Si el front quiere evitar ambiguedad, deberia imponer una sola asociacion.

### Listados disponibles

Endpoints:

- `GET /api/aportaciones`
- `GET /api/aportaciones/{id}`
- `GET /api/aportaciones/usuario/{idUsuario}`
- `GET /api/aportaciones/tema/{idTema}`
- `GET /api/aportaciones/recurso/{idRecurso}`
- `GET /api/aportaciones/reportadas`

Permisos:

- General, detalle, por tema y por recurso: cualquier autenticado.
- Por usuario: dueño o admin.
- Reportadas: solo admin.

### Editar aportacion

Endpoint:

- `PUT /api/aportaciones/{id}`

Permisos:

- Solo el autor real.

Body:

```json
{
  "contenido": "Contenido actualizado",
  "idTema": 1
}
```

Comportamiento:

- El backend vuelve a forzar `idUsuario` desde el token.
- Solo el autor puede editar.
- No se puede editar una aportacion eliminada.

### Eliminar aportacion

Endpoint:

- `DELETE /api/aportaciones/{id}`

Permisos:

- El autor o admin.

Comportamiento:

- El borrado es logico.
- La aportacion queda con `eliminada = true`.
- La aportacion deja de salir en listados normales.
- La aportacion deja de estar marcada como reportada.

### Uso frontend recomendado

Las aportaciones encajan en:

- Tab social dentro de detalle de tema.
- Tab social dentro de detalle de recurso.
- Perfil de usuario con sus aportaciones.
- Cola admin de contenido reportado.

### Estados utiles de UI

- Lista vacia.
- Formulario creando.
- Formulario validando enlaces.
- Aportacion reportada.
- Aportacion eliminada localmente.
- Error por intento de editar contenido de otro usuario.

## Modulo De Favoritos

### Que se puede favoritear

El backend permite guardar exactamente uno de estos tres destinos:

- Un recurso local por `idRecurso`.
- Una aportacion por `idAportacion`.
- Un recurso externo por `recursoExterno`.

### Endpoint de alta

- `POST /api/favoritos`

Permisos:

- Cualquier usuario autenticado.

### Casos de uso

#### Favorito de recurso local

```json
{
  "idRecurso": 1
}
```

#### Favorito de aportacion

```json
{
  "idAportacion": 1
}
```

#### Favorito de recurso externo

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

### Reglas reales

- Debe enviarse exactamente uno de `idRecurso`, `idAportacion` o `recursoExterno`.
- Si ya existe para ese usuario, devuelve `400`.
- No se puede guardar una aportacion eliminada.
- Si el favorito es externo, el backend busca primero por `urlEnlace`.
- Si no existe ese recurso en BBDD, lo crea y luego guarda el favorito.

### DTO de favorito

```json
{
  "idFavorito": 10,
  "idUsuario": 2,
  "idRecurso": 1,
  "idAportacion": null,
  "fechaGuardado": "2026-04-11T22:00:00",
  "tipoFavorito": "RECURSO",
  "tituloElemento": "Guerra del Pacifico - recurso local"
}
```

Si el favorito es una aportacion:

```json
{
  "idFavorito": 11,
  "idUsuario": 2,
  "idRecurso": null,
  "idAportacion": 5,
  "fechaGuardado": "2026-04-11T22:10:00",
  "tipoFavorito": "APORTACION",
  "tituloElemento": "Texto de la aportacion"
}
```

### Listado de favoritos

Endpoint:

- `GET /api/favoritos/usuario/{idUsuario}`

Permisos:

- El dueño o admin.

### Borrado de favorito

Endpoint:

- `DELETE /api/favoritos/{id}`

Permisos:

- El dueño o admin.

### Implicaciones para frontend

- Se puede poner boton de guardar en resultados locales.
- Se puede poner boton de guardar en resultados externos.
- Se puede poner boton de guardar en aportaciones.
- El listado de favoritos devuelve un resumen, no el objeto expandido completo del recurso o de la aportacion.
- Si se quiere una pantalla de favoritos rica, puede hacer falta navegar a detalle usando `idRecurso` o `idAportacion` cuando existan.

## Modulo De Historial

### Como se genera

El historial no se crea manualmente desde frontend. Se crea automaticamente:

- Al realizar una busqueda.
- Al abrir el detalle de un recurso.

### Contrato real

```json
{
  "idHistorial": 15,
  "terminoBusqueda": "guerra del pacifico",
  "fecha": "2026-04-11T22:20:00",
  "idUsuario": 2,
  "idRecurso": 1,
  "tituloRecurso": "Guerra del Pacifico - recurso local"
}
```

### Lectura

Endpoint:

- `GET /api/historial/usuario/{idUsuario}`

Permisos:

- El dueño o admin.

Orden:

- De mas reciente a mas antiguo.

### Borrado individual

Endpoint:

- `DELETE /api/historial/{idHistorial}`

Permisos:

- El dueño o admin.

### Borrado total

Endpoint:

- `DELETE /api/historial/usuario/{idUsuario}`

Permisos:

- El dueño o admin.

### Uso frontend recomendado

- Pantalla "historial reciente".
- Seccion dentro de perfil.
- Acciones "repetir busqueda" y "abrir recurso" si existe `idRecurso`.
- Botones de limpiar item o limpiar todo.

## Modulo De Reportes Y Moderacion

### Que se puede reportar

Solo aportaciones.

### Crear reporte

Endpoint:

- `POST /api/reportes`

Permisos:

- Cualquier usuario autenticado.

Body:

```json
{
  "idAportacion": 1,
  "motivo": "Contenido inapropiado"
}
```

Reglas:

- No se puede reportar una aportacion propia.
- No se puede reportar una aportacion eliminada.
- No se puede duplicar un reporte pendiente del mismo usuario sobre la misma aportacion.
- El reporte nace con estado `PENDIENTE`.
- La aportacion pasa a marcarse como `reportada = true`.

### DTO de reporte

```json
{
  "idReporte": 3,
  "motivo": "Contenido inapropiado",
  "estado": "PENDIENTE",
  "fecha": "2026-04-11T22:30:00",
  "idUsuarioReporta": 7,
  "idAportacion": 1,
  "nombreUsuarioReporta": "Otro Usuario",
  "contenidoAportacion": "Texto reportado"
}
```

### Consultas disponibles

Endpoints:

- `GET /api/reportes/mis-reportes`
- `GET /api/reportes/pendientes`
- `GET /api/reportes/{idReporte}`

Permisos:

- Mis reportes: cualquier autenticado.
- Pendientes: solo admin.
- Por id: creador del reporte o admin.

### Resolver reporte

Endpoint:

- `PUT /api/reportes/{idReporte}/resolver`

Permisos:

- Solo `ADMIN`.

Body valido para mantener:

```json
{
  "accion": "MANTENER"
}
```

Body valido para eliminar:

```json
{
  "accion": "ELIMINAR"
}
```

Comportamiento:

- `MANTENER`: el reporte actual pasa a `REVISADO`. Si ya no quedan pendientes para esa aportacion, `reportada` vuelve a `false`.
- `ELIMINAR`: la aportacion se elimina logicamente, deja de estar reportada y todos los reportes pendientes de esa aportacion pasan a `REVISADO`.

### Uso frontend recomendado

Zona usuario:

- Boton "reportar" en aportaciones ajenas.
- Pantalla "mis reportes".

Zona admin:

- Cola de reportes pendientes.
- Vista de detalle del reporte.
- Accion de mantener.
- Accion de eliminar aportacion.
- Vista auxiliar de aportaciones reportadas.

## Integraciones Externas

### Proveedores activos

- Wikipedia.
- Open Library.
- Apple Podcasts.
- YouTube.

### Como aprovecharlo en frontend

- No consumir proveedores externos directamente.
- Usar siempre `GET /api/busquedas`.
- Presentar `fuente` como badge.
- Reutilizar un mismo componente de card de recurso.

### Variabilidad esperable

- La cantidad de resultados puede variar segun proveedor.
- YouTube puede no devolver nada si no hay API key.
- Si una fuente externa falla, la respuesta puede seguir siendo `200` pero con menos resultados.

## Matriz De Permisos

### Publico

- Registro.
- Login.

### Usuario autenticado

- Ver y editar su perfil.
- Buscar.
- Ver temas.
- Ver recursos.
- Ver aportaciones.
- Crear, editar y borrar sus aportaciones.
- Guardar y borrar sus favoritos.
- Consultar y limpiar su historial.
- Crear reportes.
- Ver sus reportes.

### Admin

Todo lo anterior, mas:

- Listar usuarios.
- Editar rol de usuario.
- CRUD de temas.
- CRUD de recursos.
- Ver aportaciones reportadas.
- Ver reportes pendientes.
- Resolver reportes.

## Mapa De Pantallas Recomendado

## Zona Publica

### Pantalla de login

Consume:

- `POST /api/usuarios/login`

Estados:

- Inicial.
- Cargando.
- Credenciales invalidas.
- Login correcto.

### Pantalla de registro

Consume:

- `POST /api/usuarios/registro`

Estados:

- Inicial.
- Validacion cliente.
- Cargando.
- Email duplicado.
- Registro correcto.

## Zona Usuario

### Home privada

Objetivo:

- Acceso al buscador principal.
- Accesos rapidos a temas, favoritos, historial y perfil.

### Resultados de busqueda

Consume:

- `GET /api/busquedas`

Secciones:

- Recursos locales.
- Recursos externos.
- Aportaciones.

Acciones:

- Filtrar por tipo.
- Abrir recurso.
- Guardar favorito.
- Reportar una aportacion desde resultados si se muestra contexto suficiente.

### Detalle de recurso

Consume:

- `GET /api/recursos/{id}`
- `GET /api/aportaciones/recurso/{idRecurso}`
- `POST /api/favoritos`
- `POST /api/aportaciones`

Contenido recomendado:

- Metadatos del recurso.
- Enlace externo de lectura o reproduccion.
- Aportaciones asociadas.
- Boton favorito.
- Formulario de aportacion.

### Listado y detalle de tema

Consume:

- `GET /api/temas`
- `GET /api/temas/{id}`
- `GET /api/aportaciones/tema/{idTema}`

Opcionalmente:

- Filtrado de recursos por tema a nivel cliente si el front ya tiene listado de recursos.

### Perfil propio

Consume:

- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`
- `GET /api/aportaciones/usuario/{id}`
- `GET /api/favoritos/usuario/{id}`
- `GET /api/historial/usuario/{id}`

Tabs recomendadas:

- Datos personales.
- Aportaciones.
- Favoritos.
- Historial.
- Mis reportes.

### Favoritos

Consume:

- `GET /api/favoritos/usuario/{id}`
- `DELETE /api/favoritos/{id}`

Filtros recomendados:

- Todos.
- Recursos.
- Aportaciones.

### Historial

Consume:

- `GET /api/historial/usuario/{id}`
- `DELETE /api/historial/{idHistorial}`
- `DELETE /api/historial/usuario/{id}`

Acciones:

- Borrar item.
- Vaciar todo.
- Repetir busqueda.
- Abrir recurso si aplica.

### Mis reportes

Consume:

- `GET /api/reportes/mis-reportes`
- `GET /api/reportes/{idReporte}`

## Zona Admin

### Dashboard admin

Puede centralizar accesos a:

- Usuarios.
- Temas.
- Recursos.
- Reportes pendientes.
- Aportaciones reportadas.

### Gestion de usuarios

Consume:

- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`

### Gestion de temas

Consume:

- `GET /api/temas`
- `POST /api/temas`
- `PUT /api/temas/{id}`
- `DELETE /api/temas/{id}`

### Gestion de recursos

Consume:

- `GET /api/recursos`
- `POST /api/recursos`
- `PUT /api/recursos/{id}`
- `DELETE /api/recursos/{id}`

### Moderacion

Consume:

- `GET /api/reportes/pendientes`
- `GET /api/reportes/{idReporte}`
- `PUT /api/reportes/{idReporte}/resolver`
- `GET /api/aportaciones/reportadas`

## Flujos Funcionales Recomendados

### Flujo de alta y primer acceso

1. Registro.
2. Login.
3. Entrada a home privada.
4. Primera busqueda.
5. Guardar favorito o abrir recurso.

### Flujo de descubrimiento

1. Usuario busca.
2. Ve resultados locales, externos y aportaciones.
3. Filtra por tipo.
4. Abre un recurso o guarda un externo.

### Flujo de participacion

1. Usuario entra en un tema o recurso.
2. Lee aportaciones existentes.
3. Publica una nueva aportacion.
4. Si detecta contenido inapropiado, reporta.

### Flujo de organizacion personal

1. Usuario guarda favoritos.
2. Vuelve desde pantalla de favoritos.
3. Revisa historial reciente.
4. Repite busquedas desde historial.

### Flujo de moderacion admin

1. Admin abre cola de pendientes.
2. Inspecciona reporte y aportacion asociada.
3. Decide `MANTENER` o `ELIMINAR`.
4. Revisa si la cola se actualiza.

## Estados De UI Y Manejo De Errores

### Codigos mas habituales

- `200 OK`: operacion correcta con contenido.
- `201 Created`: alta correcta.
- `204 No Content`: borrado correcto.
- `400 Bad Request`: validacion, duplicado o regla de negocio.
- `401 Unauthorized`: token ausente, invalido o expirado.
- `403 Forbidden`: sin permisos.
- `404 Not Found`: recurso inexistente.

### Tratamiento recomendado por codigo

#### 400

Mostrar mensaje funcional segun contexto:

- Email ya registrado.
- Password demasiado corta.
- Tipo de recurso no valido.
- Favorito duplicado.
- No se permiten enlaces externos en aportaciones.
- Ya has reportado esta aportacion.

#### 401

- Invalidar sesion local.
- Redirigir a login.
- Mostrar mensaje de sesion expirada si aplica.

#### 403

- Mostrar pantalla o bloque de acceso denegado.
- Ocultar botones restringidos cuando el rol ya se conozca desde cliente.

#### 404

- Mostrar recurso no encontrado.
- Permitir volver atras o ir al inicio.

### Estados de carga recomendados

- Carga inicial de sesion.
- Carga de listados.
- Carga de detalle.
- Envio de formularios.
- Accion optimista en favoritos si se quiere mejorar UX.

## Tipos Recomendados En Frontend

### Usuario

```ts
type Rol = "USER" | "ADMIN";

interface Usuario {
  idUsuario: number;
  nombre: string;
  email: string;
  imagenPerfil: string | null;
  rol: Rol;
  fechaRegistro: string;
}
```

### Auth response

```ts
interface AuthResponse {
  token: string;
  usuario: Usuario;
}
```

### Tema

```ts
interface Tema {
  idTema: number;
  nombre: string;
  descripcion: string | null;
}
```

### Tipo de recurso y recurso

```ts
type TipoRecurso = "ARTICULO" | "VIDEO" | "PODCAST" | "LIBRO";

interface Recurso {
  idRecurso: number | null;
  titulo: string;
  urlEnlace: string;
  descripcion: string | null;
  tipoRecurso: TipoRecurso;
  fuente: string;
  fechaPublicacion: string | null;
  idTema: number | null;
}
```

### Aportacion

```ts
interface Aportacion {
  idAportacion: number;
  contenido: string;
  fechaCreacion: string;
  idUsuario: number;
  idTema: number | null;
  idRecurso: number | null;
  nombreUsuario: string | null;
  reportada: boolean;
  eliminada: boolean;
}
```

### Favorito

```ts
type TipoFavorito = "RECURSO" | "APORTACION";

interface Favorito {
  idFavorito: number;
  idUsuario: number;
  idRecurso: number | null;
  idAportacion: number | null;
  fechaGuardado: string;
  tipoFavorito: TipoFavorito | null;
  tituloElemento: string | null;
}
```

### Historial

```ts
interface HistorialItem {
  idHistorial: number;
  terminoBusqueda: string;
  fecha: string;
  idUsuario: number;
  idRecurso: number | null;
  tituloRecurso: string | null;
}
```

### Reporte

```ts
type EstadoReporte = "PENDIENTE" | "REVISADO";

interface Reporte {
  idReporte: number;
  motivo: string;
  estado: EstadoReporte;
  fecha: string;
  idUsuarioReporta: number;
  idAportacion: number;
  nombreUsuarioReporta: string | null;
  contenidoAportacion: string | null;
}
```

### Respuesta de busqueda

```ts
interface BusquedaResponse {
  termino: string;
  recursosLocales: Recurso[];
  recursosExternos: Recurso[];
  aportaciones: Aportacion[];
}
```

## Reglas De Negocio Que El Front Debe Respetar

- No intentar registrar roles desde formulario publico.
- No permitir passwords de menos de 6 caracteres.
- No lanzar busquedas con menos de 2 caracteres.
- No permitir seleccionar tipos de recurso fuera del enum soportado.
- No permitir enlaces externos en aportaciones.
- No mostrar accion de reportar en aportaciones propias.
- No mostrar acciones admin a usuarios no admin.
- No asumir que los recursos externos tengan `idRecurso`.
- No asumir que favoritos devuelvan recurso expandido.

## Limitaciones Actuales Del Backend

- No existe refresh token.
- No existe endpoint `/me`.
- No existe paginacion.
- No existe ordenacion configurable desde API.
- No existe subida de archivos.
- `imagenPerfil` es solo texto o URL.
- No existen comentarios anidados ni respuestas a aportaciones.
- No existen likes o valoraciones.
- No existen carpetas o colecciones de favoritos.
- No existen notificaciones.
- No existen endpoints publicos de exploracion sin login.
- No existe dashboard agregado de usuario.

## Recomendaciones De Prioridad Para El Front

### Fase 1

- Login y registro.
- Estado global de sesion.
- Home privada.
- Busqueda global.
- Resultado con secciones.
- Favoritos basicos.
- Perfil propio.

### Fase 2

- Detalle de recurso.
- Aportaciones por recurso y tema.
- Historial.
- Mis reportes.

### Fase 3

- Panel admin.
- CRUD de temas.
- CRUD de recursos.
- Gestion de usuarios.
- Moderacion de reportes.

## Conclusiones

El backend ya permite montar un frontend con bastante profundidad funcional. No se trata solo de un CRUD simple: ya existe una combinacion util de autenticacion, busqueda federada, organizacion personal, capa social y moderacion. El mejor enfoque para el frontend es diseñar una experiencia basada en esos cinco bloques y no dejar sin usar piezas que el backend ya soporta, especialmente busqueda unificada, favoritos de externos, historial automatico, aportaciones y moderacion.

Si se sigue este documento, el frontend puede quedar muy completo sin necesidad de ampliar backend en una primera iteracion.
