package week11.st6135.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import week11.st6135.finalproject.model.Note
import week11.st6135.finalproject.data.NotesRepository
import week11.st6135.finalproject.util.NotesUiState

class NotesViewModel(private val repo: NotesRepository = NotesRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Idle)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var notesFlowJob: Job? = null

    /**
     * Start collecting real-time notes for the given user id.
     */
    fun startNotesListener(userId: String) {
        notesFlowJob?.cancel()
        _uiState.value = NotesUiState.Loading

        notesFlowJob = repo.getNotesFlow(userId)
            .onEach { notes ->
                _uiState.value = NotesUiState.Success(notes)
            }
            .catch { e ->
                _uiState.value = NotesUiState.Error(e.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    fun stopNotesListener() {
        notesFlowJob?.cancel()
        notesFlowJob = null
        _uiState.value = NotesUiState.Idle
    }

    fun addNote(userId: String, title: String, text: String, onComplete: (Result<String>) -> Unit = {}) {
        viewModelScope.launch {
            val note = Note(
                id = "",
                title = title,
                text = text
            )
            val res = repo.upsertNote(userId, note)
            onComplete(res)
        }
    }

    fun updateNote(userId: String, note: Note, onComplete: (Result<String>) -> Unit = {}) {
        viewModelScope.launch {
            val res = repo.upsertNote(userId, note)
            onComplete(res)
        }
    }

    fun deleteNote(userId: String, noteId: String, onComplete: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            val res = repo.deleteNote(userId, noteId)
            onComplete(res)
        }
    }
}