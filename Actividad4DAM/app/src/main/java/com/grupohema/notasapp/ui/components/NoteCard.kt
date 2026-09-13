package com.grupohema.notasapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grupohema.notasapp.data.Note
import com.grupohema.notasapp.ui.theme.DeleteRed
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

/**
 * Tarjeta de una nota envuelta en SwipeToDismissBox: al deslizar hacia
 * cualquier lado más allá del umbral (30% del ancho, igual que
 * FractionalThreshold(0.3f) en la especificación original con
 * Modifier.swipeable) se confirma el borrado y se llama a [onDelete].
 *
 * SwipeToDismissBox es el reemplazo moderno de Material3 para el gesto
 * "swipe to dismiss" (Modifier.swipeable de Compose Material 1 quedó
 * deprecado).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteNoteCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.3f },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
            true
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { DeleteBackground(dismissState.dismissDirection) },
        content = { NoteCard(note = note, onClick = onClick, onLongClick = onLongClick) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteBackground(dismissDirection: SwipeToDismissBoxValue) {
    val alignment = if (dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
        Alignment.CenterStart
    } else {
        Alignment.CenterEnd
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 6.dp)
            .background(DeleteRed, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Eliminar nota",
            tint = Color.White
        )
    }
}

/**
 * Tap: abre el diálogo de edición. Pulsación larga: abre la vista de
 * lectura con zoom de pellizco ([NoteZoomDialog]). Al presionar, la
 * tarjeta se encoge ligeramente (animateFloatAsState) como
 * retroalimentación visual del toque.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (note.color != 0L) Color(note.color.toInt()) else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (note.color != 0L) Color.Black.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(),
        label = "noteCardPressScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = note.title.ifBlank { "(Sin título)" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (note.color != 0L) Color.Black else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 5,
                color = contentColor
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = contentColor.copy(alpha = 0.6f)
                )
                Text(
                    text = note.date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}
