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
- **`tareas-room-compose/`** — Pendiente: esta carpeta quedó como una
  referencia rota a otro repositorio (sin archivo `.gitmodules` ni URL
  registrada), por lo que al clonar este repositorio aparece vacía. Quien
  la haya creado necesita subir su contenido real aquí (o indicar la URL
  del repositorio original) para que deje de estar vacía.

## Cómo compilar la app de notas

```bash
cd Actividad4DAM
./gradlew assembleDebug
```

Requiere JDK 17 y Android SDK (API 26+). Ver
[Actividad4DAM/README.md](Actividad4DAM/README.md) para más detalle.
