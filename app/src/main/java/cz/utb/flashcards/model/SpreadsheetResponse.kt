package cz.utb.flashcards.model

data class SpreadsheetResponse(
    val range: String,
    val majorDimension: String,
    val values: List<List<String>>
)
