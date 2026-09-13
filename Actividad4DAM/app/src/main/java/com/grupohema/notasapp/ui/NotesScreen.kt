package com.grupohema.notasapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grupohema.notasapp.data.Note
import com.grupohema.notasapp.ui.components.NoteDialog
import com.grupohema.notasapp.ui.components.NoteZoomDialog
import com.grupohema.notasapp.ui.components.SwipeToDeleteNoteCard

/**
 * Pantalla principal: cuadrícula de notas + FAB para agregar.
 *
 * La cuadrícula usa [StaggeredGridCells.Adaptive], por lo que el número de
 * columnas se ajusta solo según el ancho disponible: una columna en un
 * teléfono en vertical, dos o más en tablets, pantallas grandes o al girar
 * a horizontal (diseño responsivo, sin lógica manual de breakpoints).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var noteToView by remember { mutableStateOf<Note?>(null) }
    var searchActive by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva nota") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onSearch = { searchActive = false },
                active = searchActive,
                onActiveChange = { searchActive = it },
                placeholder = { Text("Buscar notas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                // Resultados rápidos o sugerencias si se desea
            }

            Crossfade(targetState = notes.isEmpty(), label = "notesEmptyState") { isEmpty ->
                if (isEmpty) {
                    EmptyState(searchQuery.isNotEmpty())
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(minSize = 170.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalItemSpacing = 4.dp
                    ) {
                        items(notes, key = { it.id }) { note ->
                            var visible by remember(note.id) { mutableStateOf(false) }
                            LaunchedEffect(note.id) { visible = true }

                            AnimatedVisibility(
                                visible = visible,
                                enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) + fadeOut(),
                                modifier = Modifier.animateItemPlacement()
                            ) {
                                SwipeToDeleteNoteCard(
                                    note = note,
                                    onClick = { noteToEdit = note },
                                    onLongClick = { noteToView = note },
                                    onDelete = { viewModel.deleteNote(note) },
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        NoteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, color ->
                viewModel.addNote(title, content, color)
                showAddDialog = false
            }
        )
    }

    noteToEdit?.let { note ->
        NoteDialog(
            noteToEdit = note,
            onDismiss = { noteToEdit = null },
            onConfirm = { title, content, color ->
                viewModel.updateNote(note, title, content, color)
                noteToEdit = null
            }
        )
    }

    noteToView?.let { note ->
        NoteZoomDialog(note = note, onDismiss = { noteToView = null })
    }
}

@Composable
private fun EmptyState(isSearching: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isSearching) "No se encontraron notas" else "No tienes notas todavía",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (isSearching) "Intenta con otros términos" else "Toca el botón + para crear la primera.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
