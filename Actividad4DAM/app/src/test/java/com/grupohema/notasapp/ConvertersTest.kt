package com.grupohema.notasapp

import com.grupohema.notasapp.data.Converters
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

/**
 * Prueba unitaria (JVM, sin dispositivo) del TypeConverter de fechas:
 * verifica que convertir LocalDateTime -> Long -> LocalDateTime no pierda
 * información (round-trip).
 */
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `epoch millis round trip keeps the same date-time`() {
        val original = LocalDateTime.of(2026, 9, 10, 14, 30, 0)

        val millis = converters.toEpochMillis(original)
        val restored = converters.fromEpochMillis(millis)

        assertEquals(original, restored)
    }

    @Test
    fun `null values are preserved`() {
        assertEquals(null, converters.toEpochMillis(null))
        assertEquals(null, converters.fromEpochMillis(null))
    }
}
