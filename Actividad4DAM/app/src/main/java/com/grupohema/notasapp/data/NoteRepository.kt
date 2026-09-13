package com.grupohema.notasapp.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: capa intermedia entre el ViewModel y el DAO. Aísla al
 * ViewModel de los detalles de Room y facilita hacer pruebas unitarias
 * (se puede sustituir por un fake/mock en tests).
 */
class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAll()

    suspend fun insert(note: Note): Long = noteDao.insert(note)

    suspend fun update(note: Note) = noteDao.update(note)

    suspend fun delete(note: Note) = noteDao.delete(note)
}
