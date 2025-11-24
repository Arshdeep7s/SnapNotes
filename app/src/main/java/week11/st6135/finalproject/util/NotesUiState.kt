package week11.st6135.finalproject.util

import week11.st6135.finalproject.model.Note

sealed class NotesUiState {
    object Idle : NotesUiState()
    object Loading : NotesUiState()
    data class Success(val notes: List<Note>) : NotesUiState()
    data class Error(val message: String) : NotesUiState()
}