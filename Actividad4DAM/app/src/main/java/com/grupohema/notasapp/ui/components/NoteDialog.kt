package com.grupohema.notasapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.grupohema.notasapp.data.Note
import com.grupohema.notasapp.ui.theme.NoteColors

/**
 * Diálogo reutilizable para crear una nota nueva o editar una existente.
 * Si [noteToEdit] es null, se usa en modo "agregar"; si no, precarga los
 * campos con los datos de la nota a editar.
 */
@Composable
fun NoteDialog(
    noteToEdit: Note? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, color: Long) -> Unit
) {
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var content by remember { mutableStateOf(noteToEdit?.content ?: "") }
    var selectedColor by remember { mutableLongStateOf(noteToEdit?.color ?: NoteColors[0].toArgb().toLong()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (noteToEdit == null) "Nueva nota" else "Editar nota") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Text(
                    text = "Seleccionar color",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NoteColors.forEach { color ->
                        val colorLong = color.toArgb().toLong()
                        val isSelected = selectedColor == colorLong
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.2f else 1f,
                            animationSpec = spring(),
                            label = "colorSwatchScale"
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .graphicsLayer(scaleX = scale, scaleY = scale)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color.LightGray.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorLong }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, content, selectedColor) },
                enabled = title.isNotBlank() || content.isNotBlank()
            ) {
                Text(if (noteToEdit == null) "Agregar" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
