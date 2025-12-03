# Guía técnica: Rutas, Resolvers y Modales en Angular (Standalone)

Este documento resume criterios prácticos para decidir cuándo crear rutas, cuándo usar resolvers y cuándo manejar estados como modales sin ruta dedicada. Incluye pautas de implementación, checklist de cambios y ejemplos aplicados al proyecto.

## Principios
- **Ruta**: define un estado navegable con URL propio (deep link, back/forward del navegador, guardas). Úsala para páginas o vistas que tienen identidad propia.
- **Resolver**: precarga datos necesarios para que la ruta se renderice completa antes de activarse. Úsalo cuando la vista no es funcional sin esos datos.
- **Modal**: por defecto es estado interno de UI (sin ruta). Úsalo para acciones contextuales dentro de una página. Sólo conviértelo en ruta si necesitas deep-link (ej.: abrir modal directamente por URL y persistir al refrescar).

## ¿Cuándo crear una ruta?
- Sí:
  - Página o detalle con identidad propia (ej.: `/restaurantes/123`).
  - Necesitas compartir URL o restaurar estado al refrescar.
  - Requieres guardas (`canActivate`, `canMatch`) o resolvers.
- No:
  - Componente incrustado sin valor de navegación independiente.
  - Modal/diálogo que no debe abrirse directamente desde una URL.

## ¿Cuándo usar un resolver?
- Sí:
  - Los datos son **obligatorios** para renderizar la página (sin ellos la vista no funciona).
  - Quieres comportamiento consistente entre navegación interna y acceso directo por URL.
  - La llamada es **determinística y breve**; el usuario espera la página ya completa.
- No:
  - Los datos son **opcionales** o pueden cargarse después con un esqueleto/loader.
  - La carga depende de **interacción del usuario** dentro de la página.
  - Streams, polling prolongado, o llamadas pesadas que conviene manejar en el componente.

## Modales: con o sin ruta
- **Sin ruta (recomendado por defecto)**:
  - Más simple: estado local (ej.: `showLoginModal = true`).
  - No hay deep-link ni persistencia al refrescar.
- **Con ruta**:
  - Pro: deep-link y restauración de estado tras refresco.
  - Contra: coordinación extra (apertura/cierre según URL, navegación al cerrar).
  - Úsalo si necesitas `URL` como contrato: ej. compartir `.../nueva-resena?idReserva=...` para abrir modal directamente.

## Lazy loading
- Usa `loadComponent` (standalone) o `loadChildren` (módulos) para reducir el bundle inicial.
- Bueno para páginas secundarias o áreas poco frecuentes.
- Evítalo en rutas críticas del primer render si añade latencia innecesaria.

## Guards vs Resolvers
- **Guards**: controlan acceso (auth, roles, flags). Deciden si la ruta se activa.
- **Resolvers**: precargan datos una vez que la ruta está autorizada para activarse.

## Pautas de implementación (Angular 15/16+ standalone)
- Declarar rutas con `loadComponent` donde corresponda y mantenerlas **cohesivas**.
- Importar explícitamente `CommonModule`, `FormsModule` u otros en cada componente standalone.
- Centralizar headers de autenticación en un **interceptor** HTTP.
- Mantener servicios **puros y testeables**; evitar lógica de UI en servicios.
- i18n: usar `$localize` de forma consistente.

## Checklist de cambios para una nueva funcionalidad
1. **Definir URL** (si aplica): patrón claro (`/feature/:id`, query params para filtros opcionales).
2. **Decidir ruta vs modal interno**: ¿necesita deep-link? Si no, estado local.
3. **Resolver (sí/no)**: ¿los datos son obligatorios para render? Si sí, resolver; si no, loader en componente.
4. **Lazy loading**: ¿conviene para esta ruta?
5. **Guardas**: añadir `canActivate/canMatch` si requiere auth/roles.
6. **Servicios y modelos**: contratos claros, tipos fuertes, manejo de errores.
7. **UX**: esqueleto/loader si no hay resolver; mensajes de error amigables.
8. **Pruebas**: unit tests para guardas y resolvers; tests de componente para estados loading/error/success.

## Ejemplos aplicados (Proyecto Ristorino)

### Mis Reservas (ruta con resolver)
- Ruta: `/mis-reservas`.
- Resolver: carga la lista de reservas antes de activar la ruta (la vista depende de esos datos).
- Componente: lee `this.route.snapshot.data.reservas` y renderiza.

### Agregar Reseña (modal por defecto, sin resolver)
- Comportamiento recomendado: **modal interno** dentro de `Mis Reservas`.
- Si se requiere deep-link (abrir directo con URL): se puede usar ruta `/mis-reservas/nueva-resena?idReserva=...` que abre el modal al entrar y cierra navegando hacia `/mis-reservas`.
- Datos para insertar reseña: el **frontend envía sólo `idReserva`** y el backend resuelve `nro_restaurante`/`nro_sucursal`.

## Buenas prácticas de errores y navegación
- En resolvers: ante error, redirigir a una ruta de error o volver atrás con mensaje.
- En componentes: mostrar loader y error; permitir reintentos.
- En modales con ruta: al cerrar, navegar a la ruta base para limpiar el estado.

## Plantillas (snippets)

### Resolver mínimo
```ts
export const reservasResolver: ResolveFn<IReserva[]> = (route) => {
  const resource = inject(ReservaResource);
  return resource.obtenerMisReservas();
};
```

### Ruta con loadComponent y resolver
```ts
{
  path: 'mis-reservas',
  loadComponent: () => import('./main/pages/mis-reservas/mis-reservas')
    .then(m => m.MisReservasPage),
  resolve: { reservas: reservasResolver },
  canActivate: [authGuard]
}
```

### Modal interno sin ruta dedicada
```ts
// En el componente padre
showResenaModal = false;
abrirResena(idReserva: string) {
  this.showResenaModal = true;
  // pasar idReserva como input o vía servicio/contexto
}
cerrarResena() { this.showResenaModal = false; }
```

### Modal con ruta (deep-link)
```ts
// Al navegar
this.router.navigate(['/mis-reservas/nueva-resena'], { queryParams: { idReserva } });

// En el componente que contiene el modal
ngOnInit() {
  const url = this.router.url;
  this.visible = url.includes('/mis-reservas/nueva-resena');
  const qp = this.route.snapshot.queryParamMap;
  this.idReserva = qp.get('idReserva');
}
cerrar() {
  this.visible = false;
  this.router.navigate(['/mis-reservas']);
}
```

---

Este documento sirve como referencia rápida para mantener decisiones de arquitectura front-end coherentes y previsibles en el proyecto.