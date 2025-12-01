package week11.st6135.finalproject.model

data class Note(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)