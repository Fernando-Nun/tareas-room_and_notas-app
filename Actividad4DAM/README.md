# Notas App — Jetpack Compose + Room

Aplicación Android de notas (crear, ver, editar y eliminar) construida con
**Jetpack Compose**, **Room** y **arquitectura MVVM**, desarrollada en equipo
para la actividad de *Desarrollo de Aplicaciones Móviles*.

## Funcionalidades

- **CRUD completo** de notas (título, contenido, color, fecha) persistidas en
  una base de datos local SQLite mediante Room.
- **Búsqueda en tiempo real** por título o contenido.
- **Colores por nota**, elegibles desde el diálogo de agregar/editar.
- **Animaciones**:
  - Cada nota nueva aparece con `AnimatedVisibility`
    (`slideInHorizontally` + `fadeIn`).
  - Al agregar, eliminar o filtrar (buscar), las notas restantes se
    reacomodan con una animación de posición (`Modifier.animateItemPlacement()`)
    en vez de saltar de golpe a su nuevo lugar.
  - Al tocar una tarjeta, esta se encoge levemente (`animateFloatAsState`)
    como retroalimentación visual del toque.
  - Al elegir un color en el diálogo, el círculo seleccionado crece con una
    animación de resorte (`spring()`).
  - La transición entre "lista vacía" y "lista con notas" usa `Crossfade`.
- **Gestos**:
  - **Deslizar (swipe) para eliminar**: `SwipeToDismissBox`, el equivalente
    moderno de `Modifier.swipeable` (deprecado).
  - **Zoom con pellizco**: una pulsación larga sobre una nota abre una vista
    de lectura a pantalla completa ([`NoteZoomDialog`](app/src/main/java/com/grupohema/notasapp/ui/components/NoteZoomDialog.kt))
    donde se puede pellizcar para agrandar el texto (útil en notas largas o
    con letra pequeña), con doble tap para restablecer el zoom.
- **Diseño responsivo**: la lista de notas usa `LazyVerticalStaggeredGrid`
  con `StaggeredGridCells.Adaptive(minSize = 170.dp)`. El número de columnas
  se recalcula solo según el ancho disponible — una columna en un teléfono en
  vertical, dos o más en tablets, pantallas grandes o al rotar a horizontal —
  sin necesidad de lógica manual de breakpoints por dispositivo.
- **Persistencia**: los datos sobreviven a cerrar y reabrir la app (probado
  con pruebas instrumentadas contra un archivo de base de datos real, no en
  memoria — ver [Pruebas](#pruebas)).

## Decisiones de diseño

- **`StaggeredGridCells.Adaptive` sobre `WindowSizeClass`**: se eligió una
  cuadrícula adaptable en vez de calcular manualmente breakpoints de ancho
  (compact/medium/expanded), porque con notas de alto variable (por la
  cantidad de texto) un grid tipo "mampostería" (masonry) aprovecha mejor el
  espacio vertical que una grilla de celdas uniformes, y se ajusta a
  cualquier ancho de pantalla sin codificar tamaños específicos de
  dispositivo.
- **Zoom con pellizco como pulsación larga, no como el tap normal**: el tap
  normal ya abre "Editar nota" (comportamiento existente que no se quería
  romper). Se usó `combinedClickable` para separar ambos gestos sobre la
  misma tarjeta sin necesitar una pantalla o botón adicional.
- **`animateItemPlacement()` en vez de reconstruir manualmente las
  animaciones de entrada/salida**: Compose ya anima el reacomodo de
  posiciones de los ítems que permanecen en la lista; combinarlo con el
  `AnimatedVisibility` existente (para la entrada de notas nuevas) da
  transiciones fluidas sin duplicar lógica de estado.

## Arquitectura

```
app/src/main/java/com/grupohema/notasapp/
├── data/
│   ├── Note.kt              # @Entity (incluye color)
│   ├── NoteDao.kt           # @Dao (CRUD + Flow)
│   ├── NoteDatabase.kt      # RoomDatabase (singleton)
│   ├── Converters.kt        # TypeConverter LocalDateTime <-> Long
│   └── NoteRepository.kt
├── ui/
│   ├── NoteViewModel.kt     # ViewModel + Factory + búsqueda
│   ├── NotesScreen.kt       # Pantalla principal (grid responsivo + FAB + búsqueda)
│   ├── components/
│   │   ├── NoteCard.kt          # Tarjeta + SwipeToDismissBox + tap/long-press
│   │   ├── NoteDialog.kt        # Diálogo agregar/editar + selector de color
│   │   └── NoteZoomDialog.kt    # Vista de lectura con zoom de pellizco
│   └── theme/               # Material3 theme + paleta de colores de nota
└── MainActivity.kt
```

Flujo de datos: `Room (SQLite) → NoteDao (Flow) → NoteRepository →
NoteViewModel (StateFlow, combinado con la búsqueda) → NotesScreen (Compose)`.
Cualquier cambio en la base de datos se refleja solo en la UI, sin refresh
manual.

## Cómo abrir el proyecto

1. Abre Android Studio (Koala o superior recomendado).
2. `File → Open` y selecciona esta carpeta.
3. Espera a que Gradle sincronice (descargará el wrapper y dependencias
   automáticamente), o compílalo por línea de comandos con `./gradlew
   assembleDebug` (incluye `gradlew`/`gradlew.bat`, no depende de que
   Android Studio los regenere).
4. Ejecuta en un emulador o dispositivo con **Android 8.0 (API 26) o
   superior**. Para ver el diseño responsivo, prueba también en un
   emulador de tablet o rotando el dispositivo a horizontal.

## Pruebas

- `app/src/test/...ConvertersTest.kt` — prueba unitaria (JVM) del
  TypeConverter de fechas.
- `app/src/androidTest/...NoteDaoTest.kt` — pruebas instrumentadas de
  insertar, actualizar y eliminar notas, y una prueba dedicada de
  persistencia que cierra y reabre un archivo de base de datos real
  (no en memoria) para confirmar que los datos sobreviven a un reinicio.

Para correrlas: panel de Gradle en Android Studio → `app > Tasks > verification
> test` (unitarias) o `connectedAndroidTest` (instrumentadas, requiere
emulador/dispositivo conectado); o `./gradlew testDebugUnitTest
connectedDebugAndroidTest`.

Además de las pruebas automatizadas, la app se probó manualmente en un
emulador (crear, editar, eliminar con swipe, persistencia tras cerrar/reabrir,
zoom con pellizco y reacomodo de la cuadrícula en distintos tamaños de
pantalla).

## Stack técnico

- Kotlin 2.0.20
- Jetpack Compose (BOM 2024.06.00) + Material3
- Room 2.6.1 (con KSP)
- Arquitectura MVVM (ViewModel + StateFlow + Repository)
- Gradle 8.7 / AGP 8.5.2

## Colaboración

Proyecto desarrollado en equipo (ver `git log`); el reporte de la actividad,
con el detalle de cada requisito, está en
[`docs/Actividad4_NotasApp_Reporte.docx`](docs/Actividad4_NotasApp_Reporte.docx).
