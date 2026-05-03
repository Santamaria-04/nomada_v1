# Plan De Implementacion Del Frontend De Nomada

## Objetivo

Este documento convierte la especificacion funcional del backend en un plan accionable de frontend. No describe solo lo que existe, sino como construir el producto paso a paso, con orden, prioridades y dependencias claras.

El foco es responder a estas preguntas:

- Que pantallas hay que hacer.
- Que hace cada pantalla.
- Que endpoints necesita.
- Que estados de UI hay que contemplar.
- En que orden conviene construirlo.
- Que piezas son MVP y cuales pueden entrar despues.

Este plan parte del backend actual. No propone features que no esten soportadas por API salvo cuando se indique explicitamente como mejora futura.

## Documentos Relacionados

- [FRONTEND_BACKEND_SPEC.md](/Users/pablosantamariagonzalez/Desktop/ESIC/TFG/codigo/back/nomada_v1/docs/FRONTEND_BACKEND_SPEC.md)
- [ENDPOINTS.md](/Users/pablosantamariagonzalez/Desktop/ESIC/TFG/codigo/back/nomada_v1/docs/ENDPOINTS.md)
- [FRONTEND_TFG_CONTRACT.md](/Users/pablosantamariagonzalez/Desktop/ESIC/TFG/codigo/back/nomada_v1/docs/FRONTEND_TFG_CONTRACT.md)

## Objetivo De Producto

Nomada se puede plantear como una app privada para usuarios autenticados que centraliza:

- Descubrimiento de recursos.
- Consulta de contenido local y externo.
- Organizacion personal mediante favoritos e historial.
- Participacion mediante aportaciones.
- Moderacion y administracion para perfiles `ADMIN`.

El frontend deberia construirse con esta jerarquia:

1. Acceso y sesion.
2. Busqueda y consumo de contenido.
3. Organizacion personal.
4. Participacion social.
5. Administracion.

## Principios De Construccion

### Principio 1: arrancar por el flujo principal

El flujo principal del producto es:

1. Login.
2. Busqueda.
3. Ver resultados.
4. Abrir recurso o guardar favorito.

Todo lo que no ayude a soportar esto en una primera fase puede ir despues.

### Principio 2: no duplicar logica que ya resuelve el backend

La busqueda, los permisos, la agregacion de contenido externo, el historial y la moderacion ya se resuelven en backend. El frontend debe usar eso, no reconstruirlo.

### Principio 3: separar zona usuario y zona admin

Aunque compartan layout o componentes, conviene pensar desde el principio en:

- Navegacion usuario.
- Navegacion admin.
- Guards por rol.

### Principio 4: construir vistas que reutilicen componentes

Hay varias piezas que deberian ser reutilizables:

- Card de recurso.
- Card de aportacion.
- Lista vacia.
- Estado de error.
- Skeleton de carga.
- Formulario de perfil.
- Formulario de aportacion.

## Arquitectura De Navegacion Recomendada

## Zona Publica

- `/login`
- `/registro`

## Zona Privada Usuario

- `/`
- `/busqueda`
- `/temas`
- `/temas/:id`
- `/recursos/:id`
- `/favoritos`
- `/historial`
- `/perfil`
- `/perfil/aportaciones`
- `/perfil/reportes`

## Zona Admin

- `/admin`
- `/admin/usuarios`
- `/admin/usuarios/:id`
- `/admin/temas`
- `/admin/recursos`
- `/admin/reportes`
- `/admin/aportaciones-reportadas`

## Estructura De Layout Recomendada

### Layout publico

- Logo o nombre del producto.
- Acceso a login y registro.
- Sin menu complejo.

### Layout privado usuario

- Header principal.
- Buscador visible o de facil acceso.
- Navegacion a temas, favoritos, historial y perfil.
- Acceso a administracion solo si `rol === ADMIN`.

### Layout admin

- Sidebar o menu de gestion.
- Accesos a usuarios, temas, recursos y moderacion.
- Enlace de vuelta a experiencia normal.

## Orden General De Implementacion

### Fase 1: base funcional minima

- Gestion de sesion.
- Login.
- Registro.
- Home autenticada.
- Busqueda global.
- Resultados.

### Fase 2: consumo de contenido

- Detalle de recurso.
- Listado de temas.
- Detalle de tema.
- Favoritos.

### Fase 3: organizacion personal y capa social

- Perfil.
- Historial.
- Aportaciones.
- Mis reportes.

### Fase 4: administracion

- Dashboard admin.
- Usuarios.
- Temas admin.
- Recursos admin.
- Moderacion de reportes.

## Backlog Por Pantallas

Cada pantalla incluye:

- Objetivo.
- Datos que necesita.
- Endpoints.
- Componentes.
- Estados de UI.
- Acciones.
- Dependencias.
- Prioridad.
- Complejidad.

## 1. Pantalla De Login

### Objetivo

Permitir acceso al sistema y crear la sesion local del usuario.

### Endpoints

- `POST /api/usuarios/login`

### Datos que envia

- `email`
- `password`

### Datos que recibe

- `token`
- `usuario`

### Componentes recomendados

- Formulario de login.
- Campo email.
- Campo password.
- Boton entrar.
- Link a registro.
- Mensaje de error.

### Estados de UI

- Inicial.
- Cargando.
- Error de credenciales.
- Exito con redireccion.

### Acciones del usuario

- Introducir email.
- Introducir password.
- Enviar formulario.
- Ir a registro.

### Dependencias

- Ninguna.

### Prioridad

- Critica.

### Complejidad

- Baja.

### Criterios de aceptacion

- Si credenciales correctas, se guarda token y usuario.
- Si credenciales incorrectas, se muestra error claro.
- Si ya hay sesion valida, puede redirigir a home.

## 2. Pantalla De Registro

### Objetivo

Permitir crear cuenta nueva.

### Endpoints

- `POST /api/usuarios/registro`

### Datos que envia

- `nombre`
- `email`
- `password`

### Componentes recomendados

- Formulario de registro.
- Campo nombre.
- Campo email.
- Campo password.
- Confirmacion visual de validaciones.
- Link a login.

### Estados de UI

- Inicial.
- Validacion cliente.
- Cargando.
- Error por email duplicado.
- Error por password corta.
- Registro correcto.

### Acciones del usuario

- Rellenar datos.
- Enviar formulario.
- Ir a login.

### Dependencias

- Ninguna.

### Prioridad

- Critica.

### Complejidad

- Baja.

### Criterios de aceptacion

- No permite passwords de menos de 6 caracteres en cliente.
- Si el backend responde `201`, informa de cuenta creada.
- Redirige a login o propone acceder.

## 3. Shell Privada / Home Autenticada

### Objetivo

Servir como punto de entrada una vez iniciada la sesion y dar acceso rapido al flujo principal.

### Endpoints

- No necesita uno especifico para renderizar si se usa el usuario guardado.

### Componentes recomendados

- Header con nombre y avatar.
- Input principal de busqueda.
- Accesos a temas.
- Accesos a favoritos.
- Accesos a historial.
- Acceso a perfil.
- Acceso admin condicional.

### Estados de UI

- Cargando sesion.
- Sin sesion.
- Sesion activa.

### Acciones del usuario

- Lanzar busqueda.
- Navegar a modulos principales.
- Cerrar sesion.

### Dependencias

- Login completado.

### Prioridad

- Critica.

### Complejidad

- Media.

### Criterios de aceptacion

- Si no hay token, redirige a login.
- Si hay token y usuario, permite navegar al resto del producto.

## 4. Pantalla De Resultados De Busqueda

### Objetivo

Mostrar el resultado unificado de la busqueda, agrupado por tipo de contenido.

### Endpoints

- `GET /api/busquedas?termino=...`
- `GET /api/busquedas?termino=...&tipos=VIDEO,LIBRO`

### Datos que necesita

- Parametro `termino`.
- Parametro opcional `tipos`.

### Datos que recibe

- `termino`
- `recursosLocales`
- `recursosExternos`
- `aportaciones`

### Componentes recomendados

- Barra de busqueda reutilizable.
- Filtros por tipo.
- Bloque de recursos locales.
- Bloque de recursos externos.
- Bloque de aportaciones.
- Card de recurso.
- Card de aportacion.
- Empty states por bloque.

### Estados de UI

- Sin termino.
- Cargando.
- Error.
- Resultado sin coincidencias.
- Resultado parcial.
- Resultado con contenido en varias secciones.

### Acciones del usuario

- Buscar.
- Filtrar por tipo.
- Abrir recurso local.
- Guardar favorito de recurso local.
- Guardar favorito de recurso externo.
- Navegar a tema o recurso relacionado si el diseño lo soporta.

### Dependencias

- Sesion activa.

### Prioridad

- Critica.

### Complejidad

- Alta.

### Criterios de aceptacion

- La pantalla separa claramente locales, externos y aportaciones.
- Si el tipo filtrado es valido, la UI actualiza resultados.
- Si el backend devuelve `400`, se informa que el filtro es invalido o que el termino es demasiado corto.

## 5. Pantalla De Detalle De Recurso

### Objetivo

Mostrar un recurso local y permitir interactuar con su contexto.

### Endpoints

- `GET /api/recursos/{id}`
- `GET /api/aportaciones/recurso/{idRecurso}`
- `POST /api/favoritos`
- `POST /api/aportaciones`

### Datos que necesita

- `idRecurso`

### Componentes recomendados

- Cabecera del recurso.
- Meta info: tipo, fuente, fecha, tema.
- CTA para abrir `urlEnlace`.
- Boton de favorito.
- Lista de aportaciones asociadas.
- Formulario de nueva aportacion.

### Estados de UI

- Cargando detalle.
- Recurso no encontrado.
- Error de red.
- Sin aportaciones.
- Guardando favorito.
- Enviando aportacion.

### Acciones del usuario

- Abrir enlace del recurso.
- Guardar en favoritos.
- Leer aportaciones.
- Publicar aportacion.
- Reportar aportacion ajena.

### Dependencias

- Busqueda o navegacion previa.

### Prioridad

- Alta.

### Complejidad

- Alta.

### Criterios de aceptacion

- Al entrar a detalle, el recurso se ve completo.
- Se pueden ver sus aportaciones.
- Se puede publicar una nueva aportacion valida.
- Se puede marcar como favorito.

## 6. Pantalla De Listado De Temas

### Objetivo

Ofrecer acceso navegable a las categorias o temas disponibles.

### Endpoints

- `GET /api/temas`

### Componentes recomendados

- Grid o lista de temas.
- Busqueda o filtro cliente opcional.
- Card de tema.

### Estados de UI

- Cargando.
- Lista vacia.
- Error.

### Acciones del usuario

- Abrir detalle de tema.

### Dependencias

- Sesion activa.

### Prioridad

- Alta.

### Complejidad

- Baja.

### Criterios de aceptacion

- El usuario puede ver todos los temas disponibles.
- Cada tema navega a su detalle.

## 7. Pantalla De Detalle De Tema

### Objetivo

Mostrar informacion del tema y su capa social asociada.

### Endpoints

- `GET /api/temas/{id}`
- `GET /api/aportaciones/tema/{idTema}`

### Componentes recomendados

- Cabecera de tema.
- Descripcion.
- Lista de aportaciones del tema.
- Formulario de nueva aportacion ligada al tema.

### Estados de UI

- Cargando.
- Tema no encontrado.
- Sin aportaciones.
- Error.

### Acciones del usuario

- Leer aportaciones.
- Crear aportacion.
- Reportar aportacion.

### Dependencias

- Listado de temas.

### Prioridad

- Alta.

### Complejidad

- Media.

### Criterios de aceptacion

- El tema muestra su informacion.
- Se ven aportaciones asociadas.
- Se puede contribuir con una nueva aportacion.

## 8. Pantalla De Favoritos

### Objetivo

Permitir al usuario revisar y gestionar sus elementos guardados.

### Endpoints

- `GET /api/favoritos/usuario/{idUsuario}`
- `DELETE /api/favoritos/{id}`

### Componentes recomendados

- Listado de favoritos.
- Filtro por tipo.
- Card resumida de favorito.
- Boton eliminar.

### Estados de UI

- Cargando.
- Vacia.
- Error.
- Eliminando favorito.

### Acciones del usuario

- Filtrar favoritos.
- Eliminar favorito.
- Navegar a detalle si hay destino resoluble.

### Dependencias

- Sesion activa.
- Usuario autenticado disponible para conocer `idUsuario`.

### Prioridad

- Alta.

### Complejidad

- Media.

### Criterios de aceptacion

- El usuario ve sus favoritos.
- Puede eliminarlos.
- La UI distingue recursos y aportaciones.

## 9. Pantalla De Perfil

### Objetivo

Centralizar identidad, configuracion basica y acceso a informacion personal.

### Endpoints

- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`

### Componentes recomendados

- Cabecera de perfil.
- Formulario editable.
- Acceso a aportaciones propias.
- Acceso a favoritos.
- Acceso a historial.
- Acceso a mis reportes.
- Accion de eliminar cuenta.

### Estados de UI

- Cargando perfil.
- Guardando cambios.
- Error de validacion.
- Error de permisos.
- Cuenta eliminada.

### Acciones del usuario

- Editar nombre.
- Editar email.
- Cambiar password.
- Cambiar imagenPerfil.
- Eliminar cuenta.

### Dependencias

- Sesion activa.

### Prioridad

- Alta.

### Complejidad

- Media.

### Criterios de aceptacion

- El usuario puede actualizar sus datos.
- Si elimina cuenta propia, la sesion se limpia.

## 10. Pantalla De Historial

### Objetivo

Permitir revisar actividad reciente y reutilizarla.

### Endpoints

- `GET /api/historial/usuario/{idUsuario}`
- `DELETE /api/historial/{idHistorial}`
- `DELETE /api/historial/usuario/{idUsuario}`

### Componentes recomendados

- Lista cronologica.
- Item de historial.
- Boton limpiar item.
- Boton limpiar todo.

### Estados de UI

- Cargando.
- Sin historial.
- Error.
- Borrando item.
- Vaciando historial.

### Acciones del usuario

- Repetir busqueda.
- Abrir recurso.
- Borrar item.
- Borrar todo.

### Dependencias

- Sesion activa.

### Prioridad

- Media.

### Complejidad

- Media.

### Criterios de aceptacion

- Se muestran busquedas y consultas recientes.
- El usuario puede limpiar una entrada o todo el historial.

## 11. Pantalla De Mis Aportaciones

### Objetivo

Permitir al usuario revisar lo que ha publicado.

### Endpoints

- `GET /api/aportaciones/usuario/{idUsuario}`
- `PUT /api/aportaciones/{id}`
- `DELETE /api/aportaciones/{id}`

### Componentes recomendados

- Lista de aportaciones propias.
- Card de aportacion.
- Accion editar.
- Accion eliminar.

### Estados de UI

- Cargando.
- Sin aportaciones.
- Editando.
- Eliminando.
- Error.

### Acciones del usuario

- Ver aportaciones propias.
- Editarlas.
- Eliminarlas logicamente.

### Dependencias

- Perfil o acceso desde menu personal.

### Prioridad

- Media.

### Complejidad

- Media.

### Criterios de aceptacion

- El usuario puede mantener su contenido sin salir a otras pantallas.

## 12. Pantalla De Mis Reportes

### Objetivo

Dar visibilidad al usuario de las denuncias o reportes que ha enviado.

### Endpoints

- `GET /api/reportes/mis-reportes`
- `GET /api/reportes/{idReporte}`

### Componentes recomendados

- Listado de reportes.
- Estado visual `PENDIENTE` o `REVISADO`.
- Modal o vista de detalle.

### Estados de UI

- Cargando.
- Sin reportes.
- Error.

### Acciones del usuario

- Ver estado del reporte.
- Consultar detalle.

### Dependencias

- Sesion activa.

### Prioridad

- Media.

### Complejidad

- Baja.

### Criterios de aceptacion

- El usuario sabe si sus reportes siguen pendientes o ya fueron revisados.

## 13. Dashboard Admin

### Objetivo

Ofrecer punto de entrada a la gestion del sistema.

### Endpoints

- No requiere uno propio si solo enlaza a modulos.

### Componentes recomendados

- Tarjetas o accesos a usuarios.
- Accesos a temas.
- Accesos a recursos.
- Accesos a reportes.
- Accesos a aportaciones reportadas.

### Estados de UI

- Verificando rol.
- Acceso denegado.
- Vista admin lista.

### Acciones del usuario

- Navegar a cada modulo de administracion.

### Dependencias

- Usuario `ADMIN`.

### Prioridad

- Alta en fase admin.

### Complejidad

- Baja.

### Criterios de aceptacion

- Un `USER` no puede entrar.
- Un `ADMIN` accede a todos los modulos de gestion.

## 14. Gestion De Usuarios Admin

### Objetivo

Permitir al admin revisar y editar usuarios.

### Endpoints

- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`

### Componentes recomendados

- Tabla de usuarios.
- Filtros cliente.
- Formulario de edicion.
- Selector de rol.

### Estados de UI

- Cargando lista.
- Sin usuarios.
- Error.
- Editando usuario.
- Eliminando usuario.

### Acciones del usuario

- Consultar usuarios.
- Editar rol.
- Editar datos.
- Eliminar usuario.

### Dependencias

- Dashboard admin.

### Prioridad

- Alta en fase admin.

### Complejidad

- Media.

### Criterios de aceptacion

- El admin puede listar y modificar usuarios.
- El rol solo se expone en esta vista admin.

## 15. Gestion De Temas Admin

### Objetivo

Mantener la taxonomia del sistema.

### Endpoints

- `GET /api/temas`
- `POST /api/temas`
- `PUT /api/temas/{id}`
- `DELETE /api/temas/{id}`

### Componentes recomendados

- Tabla o lista de temas.
- Formulario crear.
- Formulario editar.
- Confirmacion de borrado.

### Estados de UI

- Cargando.
- Sin temas.
- Creando.
- Editando.
- Borrando.
- Error.

### Acciones del usuario

- Crear tema.
- Editar tema.
- Eliminar tema.

### Dependencias

- Dashboard admin.

### Prioridad

- Alta en fase admin.

### Complejidad

- Baja.

### Criterios de aceptacion

- El admin puede mantener el catalogo de temas sin salir del panel.

## 16. Gestion De Recursos Admin

### Objetivo

Permitir al admin mantener el catalogo local de recursos.

### Endpoints

- `GET /api/recursos`
- `POST /api/recursos`
- `PUT /api/recursos/{id}`
- `DELETE /api/recursos/{id}`
- `GET /api/temas`

### Componentes recomendados

- Tabla o grid de recursos.
- Formulario de recurso.
- Selector de tema.
- Selector de tipo.

### Estados de UI

- Cargando lista.
- Vacia.
- Creando.
- Editando.
- Eliminando.
- Error de validacion.

### Acciones del usuario

- Crear recurso.
- Editar recurso.
- Eliminar recurso.

### Dependencias

- Dashboard admin.
- Temas disponibles.

### Prioridad

- Alta en fase admin.

### Complejidad

- Media.

### Criterios de aceptacion

- El admin puede crear recursos validos.
- La UI impide fechas futuras y tipos invalidos.

## 17. Moderacion De Reportes Admin

### Objetivo

Resolver los reportes pendientes de aportaciones.

### Endpoints

- `GET /api/reportes/pendientes`
- `GET /api/reportes/{idReporte}`
- `PUT /api/reportes/{idReporte}/resolver`
- `GET /api/aportaciones/reportadas`

### Componentes recomendados

- Cola de reportes pendientes.
- Vista de detalle.
- Accion `MANTENER`.
- Accion `ELIMINAR`.

### Estados de UI

- Cargando.
- Sin pendientes.
- Error.
- Resolviendo reporte.

### Acciones del usuario

- Revisar reporte.
- Mantener aportacion.
- Eliminar aportacion.

### Dependencias

- Dashboard admin.

### Prioridad

- Alta en fase admin.

### Complejidad

- Media.

### Criterios de aceptacion

- El admin puede procesar los reportes sin salir del panel.
- La lista se actualiza tras resolver.

## 18. Vista De Aportaciones Reportadas Admin

### Objetivo

Dar una vista adicional del contenido marcado como reportado.

### Endpoints

- `GET /api/aportaciones/reportadas`

### Componentes recomendados

- Lista de aportaciones reportadas.
- Enlace o CTA a su moderacion.

### Estados de UI

- Cargando.
- Sin aportaciones reportadas.
- Error.

### Acciones del usuario

- Revisar contenido marcado.
- Ir a reportes relacionados.

### Dependencias

- Dashboard admin.

### Prioridad

- Media en fase admin.

### Complejidad

- Baja.

### Criterios de aceptacion

- El admin tiene una entrada complementaria para detectar contenido conflictivo.

## Backlog Por Fases

## Fase 1: MVP Navegable

Objetivo:

- Poder acceder, mantener sesion y buscar.

Incluye:

- Login.
- Registro.
- Shell privada.
- Busqueda global.
- Resultados.
- Guards de autenticacion.
- Manejo basico de `401`.

Resultado esperado:

- Un usuario puede entrar y usar el flujo principal de descubrimiento.

## Fase 2: Consumo Y Guardado

Objetivo:

- Permitir que el usuario no solo busque, sino que consuma y guarde.

Incluye:

- Detalle de recurso.
- Listado de temas.
- Detalle de tema.
- Favoritos.

Resultado esperado:

- El usuario ya puede explorar con mas profundidad y guardar contenido.

## Fase 3: Perfil Y Capa Social

Objetivo:

- Activar identidad completa y participacion.

Incluye:

- Perfil.
- Historial.
- Mis aportaciones.
- Crear, editar y borrar aportaciones.
- Reportar aportaciones.
- Mis reportes.

Resultado esperado:

- El usuario ya tiene experiencia personal completa.

## Fase 4: Administracion

Objetivo:

- Activar la gestion del sistema.

Incluye:

- Dashboard admin.
- Gestion de usuarios.
- Gestion de temas.
- Gestion de recursos.
- Moderacion de reportes.
- Vista de aportaciones reportadas.

Resultado esperado:

- El sistema puede mantenerse y moderarse desde el frontend.

## Checklist De Implementacion

## Base tecnica

- Crear cliente HTTP centralizado.
- Inyectar token JWT automaticamente.
- Manejar `401` globalmente.
- Crear capa de tipos TypeScript.
- Crear guards de autenticacion.
- Crear guards de rol admin.

## Componentes base

- Boton.
- Input.
- Select.
- Textarea.
- Modal.
- Card de recurso.
- Card de aportacion.
- Lista vacia.
- Skeleton.
- Banner de error.

## Estado global

- Sesion.
- Usuario.
- Estado de carga por pantalla o por request.
- Notificaciones de exito o error.

## Modulo auth

- Login.
- Registro.
- Logout.
- Rehidratacion de sesion.

## Modulo busqueda

- Formulario.
- Query params.
- Persistencia opcional del termino en URL.
- Filtros por tipo.

## Modulo recursos

- Listado reutilizable de cards.
- Detalle.
- Boton favorito.

## Modulo aportaciones

- Crear.
- Editar.
- Eliminar.
- Reportar.

## Modulo usuario

- Perfil.
- Favoritos.
- Historial.
- Mis reportes.

## Modulo admin

- Layout admin.
- Gestion de usuarios.
- Gestion de temas.
- Gestion de recursos.
- Moderacion.

## Dependencias Entre Pantallas

### Dependencias criticas

- Login antes de cualquier vista privada.
- Usuario autenticado antes de favoritos, historial, perfil y aportaciones.
- Temas antes de crear o editar recursos admin con tema asociado.
- Recurso o tema cargado antes de crear aportaciones contextualizadas.
- Rol admin antes de abrir cualquier vista de gestion.

### Dependencias de experiencia

- Busqueda antes de detalle de recurso en flujo principal.
- Perfil antes de mis aportaciones si se agrupan dentro de perfil.
- Dashboard admin antes de modulos internos si se organiza con hub central.

## Riesgos Y Decisiones Que Conviene Cerrar Antes De Empezar

### Decision 1: una sola app o separar area admin

Opciones:

- Mantener una sola SPA con rutas admin.
- Separar visualmente la experiencia admin.

Recomendacion:

- Una sola app con layout admin diferenciado.

### Decision 2: perfil en una sola pantalla o con tabs

Opciones:

- Pantalla unica larga.
- Tabs para datos, favoritos, historial, aportaciones y reportes.

Recomendacion:

- Tabs o subrutas. Hay suficiente contenido como para no mezclarlo todo.

### Decision 3: busqueda en home o en pagina dedicada

Opciones:

- Home con resultados embebidos.
- Home ligera y pagina `/busqueda` dedicada.

Recomendacion:

- Home ligera y pagina de resultados dedicada.

### Decision 4: favoritos e historial dentro de perfil o como vistas propias

Recomendacion:

- Vistas propias y acceso tambien desde perfil.

## Definicion De MVP Real

Si hubiera que recortar mucho y salir rapido, el MVP minimo serio seria:

- Login.
- Registro.
- Home privada.
- Busqueda.
- Resultados.
- Detalle de recurso.
- Favoritos.
- Perfil basico.

Eso ya permitiria demostrar valor principal del producto.

## Definicion De V1 Completa

La V1 completa sobre el backend actual seria:

- Todo el MVP.
- Temas.
- Historial.
- Aportaciones.
- Reportes.
- Admin completo.

## Recomendacion Final De Ejecucion

El mejor orden practico es:

1. Sesion y rutas protegidas.
2. Busqueda y resultados.
3. Detalle de recurso y favoritos.
4. Temas.
5. Perfil e historial.
6. Aportaciones y reportes.
7. Administracion.

La razon es simple:

- Primero se habilita el flujo que mas valor demuestra.
- Despues se activa el valor de retencion.
- Luego se añade la capa social.
- Por ultimo se construye la gestion avanzada.

## Entregable Esperado Si Se Sigue Este Plan

Si se implementa siguiendo este orden, el equipo deberia acabar con:

- Un frontend navegable y protegido por roles.
- Un flujo principal bien resuelto.
- Una experiencia personal completa.
- Una capa social operativa.
- Un panel admin funcional.

Eso encaja bastante bien con el backend actual y evita tanto el infrauso como el sobre diseno.
