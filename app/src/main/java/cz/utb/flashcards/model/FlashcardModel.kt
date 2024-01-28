package cz.utb.flashcards.model

data class FlashcardModel(
    val english: String,
    val czech: String,
    val imageURL: String? = null
)

