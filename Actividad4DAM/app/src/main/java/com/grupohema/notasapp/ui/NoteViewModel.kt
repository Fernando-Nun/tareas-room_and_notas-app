package com.grupohema.notasapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grupohema.notasapp.data.Note
import com.grupohema.notasapp.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * ViewModel que expone el estado de las notas a la UI (Compose) y traduce
 * las acciones del usuario (agregar, editar, eliminar) en llamadas
 * suspend al repositorio, ejecutadas en viewModelScope.
 */
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val notes: StateFlow<List<Note>> = repository.getAllNotes()
        .combine(_searchQuery) { notes, query ->
            if (query.isBlank()) {
                notes
            } else {
                notes.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.content.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addNote(title: String, content: String, color: Long) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            repository.insert(
                Note(
                    title = title.trim(),
                    content = content.trim(),
                    date = LocalDateTime.now(),
                    color = color
                )
            )
        }
    }

    fun updateNote(note: Note, newTitle: String, newContent: String, newColor: Long) {
        viewModelScope.launch {
            repository.update(
                note.copy(
                    title = newTitle.trim(),
                    content = newContent.trim(),
                    color = newColor
                )
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}

/**
 * Factory manual: NoteViewModel necesita un NoteRepository en su
 * constructor, y ViewModelProvider por defecto solo sabe crear
 * ViewModels sin argumentos.
 */
class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
    }
}
