package com.grupohema.notasapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) con las operaciones CRUD sobre la tabla "notes".
 *
 * getAll() devuelve un Flow: Room emite automáticamente una nueva lista cada
 * vez que la tabla cambia, así la UI (Compose) se recompone sola sin tener
 * que hacer refresh manual.
 */
@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAll(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getById(noteId: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}
