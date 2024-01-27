package cz.utb.flashcards.model

data class FlashcardModel(
    val english: String,
    val czech: String
)

class FlascardsRepository(private val apiURL: String) {
//    fun getFlashCards(): FlashcardModel {
//
//    }
}