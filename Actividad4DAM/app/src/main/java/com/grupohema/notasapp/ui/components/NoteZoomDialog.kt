package com.grupohema.notasapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.grupohema.notasapp.data.Note
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
private const val MIN_SCALE = 1f
private const val MAX_SCALE = 4f

/**
 * Vista de lectura a pantalla completa con zoom de pellizco: permite
 * agrandar el texto de una nota (útil para notas largas o letra pequeña)
 * pellizcando con dos dedos, con desplazamiento (pan) mientras está
 * ampliada. Doble tap restablece el zoom a su tamaño original.
 *
 * Se abre con una pulsación larga sobre una tarjeta (ver
 * [SwipeToDeleteNoteCard]), separado del tap normal que abre "Editar".
 */
@Composable
fun NoteZoomDialog(note: Note, onDismiss: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
                        scale = newScale
                        offset = if (newScale == 1f) 0f else offset + pan.x
                        offsetY = if (newScale == 1f) 0f else offsetY + pan.y
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        scale = 1f
                        offset = 0f
                        offsetY = 0f
                    })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 64.dp, bottom = 24.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset,
                        translationY = offsetY
                    )
            ) {
                Text(
                    text = note.title.ifBlank { "(Sin título)" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = note.date.format(dateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            IconButton(onClick = onDismiss, modifier = Modifier.statusBarsPadding().padding(8.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }

            if (scale <= 1f) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOutMap,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Text(
                        text = "  Pellizca para hacer zoom",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
