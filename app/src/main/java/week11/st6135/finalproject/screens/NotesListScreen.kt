package week11.st6135.finalproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st6135.finalproject.model.Note
import week11.st6135.finalproject.util.NotesUiState
import week11.st6135.finalproject.viewmodel.NotesViewModel

@Composable
fun NotesListScreen(
    userId: String,
    notesViewModel: NotesViewModel,
    onAddNote: () -> Unit,
    onOpenNote: (Note) -> Unit
) {
    val state by notesViewModel.uiState.collectAsState()

    /*LaunchedEffect(userId) {
        notesViewModel.startNotesListener(userId)
    }
    DisposableEffect(Unit) {
        onDispose { notesViewModel.stopNotesListener() }
    }*/

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Your Notes", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onAddNote) { Text("Add") }
        }

        Spacer(Modifier.height(12.dp))

        when (state) {
            is NotesUiState.Loading -> CircularProgressIndicator()
            is NotesUiState.Error -> Text("Error: ${(state as NotesUiState.Error).message}", color = MaterialTheme.colorScheme.error)
            is NotesUiState.Success -> {
                val notes = (state as NotesUiState.Success).notes
                if (notes.isEmpty()) {
                    Text("No notes yet. Tap Add to create one.")
                } else {
                    LazyColumn {
                        items(notes) { note ->
                            NoteCard(note = note, onClick = { onOpenNote(note) }, onDelete = {
                                notesViewModel.deleteNote(userId, note.id) { /* optional callback */ }
                            })
                            Divider()
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(note.title.ifBlank { note.text.take(40) }, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(note.text, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onDelete) { Text("Delete") }
        }
    }
}
