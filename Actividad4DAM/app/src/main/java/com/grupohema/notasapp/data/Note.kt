package com.grupohema.notasapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entidad de Room que representa una nota.
 *
 * Cada nota se guarda en la tabla "notes" de la base de datos local (SQLite,
 * administrado por Room). El campo [date] usa LocalDateTime y requiere un
 * TypeConverter (ver [Converters]) porque Room solo entiende tipos primitivos.
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: LocalDateTime,
    val color: Long = 0L
)
