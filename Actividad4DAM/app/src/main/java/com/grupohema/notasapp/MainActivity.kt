package com.grupohema.notasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupohema.notasapp.data.NoteDatabase
import com.grupohema.notasapp.data.NoteRepository
import com.grupohema.notasapp.ui.NoteViewModel
import com.grupohema.notasapp.ui.NoteViewModelFactory
import com.grupohema.notasapp.ui.NotesScreen
import com.grupohema.notasapp.ui.theme.NotasAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Se construye la base de datos y el repositorio una sola vez y se
        // inyectan "a mano" en el ViewModel mediante la factory.
        val database = NoteDatabase.getInstance(applicationContext)
        val repository = NoteRepository(database.noteDao())
        val factory: ViewModelProvider.Factory = NoteViewModelFactory(repository)

        setContent {
            NotasAppTheme {
                NotasApp(factory = factory)
            }
        }
    }
}

@Composable
private fun NotasApp(factory: ViewModelProvider.Factory) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val viewModel: NoteViewModel = viewModel(factory = factory)
        NotesScreen(viewModel = viewModel)
    }
}
