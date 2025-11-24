package week11.st6135.finalproject.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import week11.st6135.finalproject.model.Note
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class NotesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun notesCollectionRef(userId: String) =
        firestore.collection("users").document(userId).collection("notes")

    /**
     * Real-time notes stream for a user.
     * Emits List<Note> whenever notes change.
     */
    fun getNotesFlow(userId: String): Flow<List<Note>> = callbackFlow {
        val ref = notesCollectionRef(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // propagate error via closing the flow
                close(error)
                return@addSnapshotListener
            }

            val notes = snapshot?.documents
                ?.mapNotNull { doc ->
                    try {
                        val note = doc.toObject(Note::class.java)
                        // ensure id field matches doc id (in case it was blank)
                        note?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

            try {
                trySend(notes).isSuccess
            } catch (t: Throwable) {
                // ignore send failures
            }
        }

        awaitClose { listener.remove() }
    }

    /**
     * Create or update a note (upsert).
     * If note.id is blank -> add new document and return generated id.
     */
    suspend fun upsertNote(userId: String, note: Note): Result<String> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                try {
                    val col = notesCollectionRef(userId)
                    if (note.id.isBlank()) {
                        // add new
                        val data = note.copy(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
                        col.add(data)
                            .addOnSuccessListener { ref ->
                                cont.resume(Result.success(ref.id))
                            }
                            .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
                    } else {
                        // update existing
                        val docRef = col.document(note.id)
                        val data = note.copy(updatedAt = System.currentTimeMillis())
                        docRef.set(data)
                            .addOnSuccessListener { cont.resume(Result.success(note.id)) }
                            .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
                    }
                } catch (e: Exception) {
                    cont.resume(Result.failure(e))
                }
            }
        }

    /**
     * Delete a note by id.
     */
    suspend fun deleteNote(userId: String, noteId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                try {
                    notesCollectionRef(userId).document(noteId)
                        .delete()
                        .addOnSuccessListener { cont.resume(Result.success(Unit)) }
                        .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
                } catch (e: Exception) {
                    cont.resume(Result.failure(e))
                }
            }
        }

    /**
     * Read a single note once (helper).
     */
    suspend fun getNoteOnce(userId: String, noteId: String): Result<Note> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                notesCollectionRef(userId).document(noteId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val note = doc.toObject(Note::class.java)?.copy(id = doc.id)
                        if (note != null) cont.resume(Result.success(note))
                        else cont.resume(Result.failure(NoSuchElementException("Note not found")))
                    }
                    .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
            }
        }
}
