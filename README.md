# tareas-room_and_notas-app

Repositorio del equipo **Pingüino** (Luis Fernando Núñez Díaz, Ricardo Baranda
Cisneros, Bryan David Mariñelarena Ponce) para las actividades de *Desarrollo
de Aplicaciones Móviles* con Room + Jetpack Compose.

## Contenido

- **[`Actividad4DAM/`](Actividad4DAM/)** — App de notas (Actividad 4): CRUD
  completo con Room, animaciones, gestos (swipe para eliminar, zoom con
  pellizco al mantener presionada una nota) y diseño responsivo. Ver el
  [README de la app](Actividad4DAM/README.md) para arquitectura, cómo
  compilarla y las pruebas incluidas.
- **[`Documentación/`](Documentación/)** — Reporte de la actividad (Word y
  PDF) con la descripción de cada requisito cumplido.
- **[`tareas-room-compose/`](tareas-room-compose/)** — App de lista de tareas: Proyecto completo desarrollado con Jetpack Compose y Room para la gestión y persistencia de tareas locales. Incluye la interfaz de usuario moderna, arquitectura recomendada y el manejo de la base de datos local.


## Cómo compilar la app de notas

```bash
cd Actividad4DAM
./gradlew assembleDebug
```

Requiere JDK 17 y Android SDK (API 26+). Ver
[Actividad4DAM/README.md](Actividad4DAM/README.md) para más detalle.
