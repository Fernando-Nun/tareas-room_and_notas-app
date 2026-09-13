package com.grupohema.notasapp.data

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Room solo puede persistir tipos de columna simples (Long, String, etc.),
 * por lo que necesitamos convertir LocalDateTime <-> Long (epoch millis en UTC)
 * para poder guardarlo en la columna "date" de la tabla notes.
 */
class Converters {

    @TypeConverter
    fun fromEpochMillis(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
        }
    }

    @TypeConverter
    fun toEpochMillis(date: LocalDateTime?): Long? {
        return date?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }
}
