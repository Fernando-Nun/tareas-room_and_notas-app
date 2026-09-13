package com.grupohema.notasapp

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.grupohema.notasapp.data.Note
import com.grupohema.notasapp.data.NoteDao
import com.grupohema.notasapp.data.NoteDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime

/**
 * Pruebas instrumentadas (corren en un dispositivo/emulador real) de las
 * operaciones CRUD y de que los datos sobreviven a reabrir la base de
 * datos (persistencia).
 *
 * Corresponde al punto "Pruebas y ajustes: Validar persistencia de datos
 * al reiniciar la aplicación" de la actividad.
 */
@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var noteDao: NoteDao
    private lateinit var db: NoteDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).build()
        noteDao = db.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndReadNote() = runBlocking {
        val note = Note(title = "Compras", content = "Leche, huevo, pan", date = LocalDateTime.now())
        noteDao.insert(note)

        val notes = noteDao.getAll().first()
        assertEquals(1, notes.size)
        assertEquals("Compras", notes[0].title)
    }

    @Test
    @Throws(Exception::class)
    fun updateNote() = runBlocking {
        val note = Note(title = "Original", content = "contenido", date = LocalDateTime.now())
        val id = noteDao.insert(note)

        val saved = noteDao.getById(id)!!
        noteDao.update(saved.copy(title = "Editado"))

        val updated = noteDao.getById(id)
        assertEquals("Editado", updated?.title)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNote() = runBlocking {
        val note = Note(title = "Temporal", content = "borrar esto", date = LocalDateTime.now())
        val id = noteDao.insert(note)
        val saved = noteDao.getById(id)!!

        noteDao.delete(saved)

        assertNull(noteDao.getById(id))
    }

    /**
     * Las pruebas de arriba usan una base de datos en memoria (se borra al
     * cerrarla), así que no prueban persistencia real. Esta prueba usa un
     * archivo de base de datos de verdad: inserta una nota, cierra la
     * conexión (simulando que la app se cierra) y abre una conexión nueva
     * apuntando al mismo archivo (simulando reabrir la app), verificando
     * que la nota siga ahí. Corresponde al punto "Validar persistencia de
     * datos al reiniciar la aplicación" de la actividad.
     */
    @Test
    @Throws(Exception::class)
    fun dataSurvivesClosingAndReopeningTheDatabaseFile() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbName = "persistence-test-db"
        context.deleteDatabase(dbName)

        try {
            val firstDb = Room.databaseBuilder(context, NoteDatabase::class.java, dbName).build()
            val id = firstDb.noteDao().insert(
                Note(title = "Persistente", content = "sigue aqui", date = LocalDateTime.now())
            )
            firstDb.close()

            val reopenedDb = Room.databaseBuilder(context, NoteDatabase::class.java, dbName).build()
            val reloaded = reopenedDb.noteDao().getById(id)
            reopenedDb.close()

            assertEquals("Persistente", reloaded?.title)
        } finally {
            context.deleteDatabase(dbName)
        }
    }
}
