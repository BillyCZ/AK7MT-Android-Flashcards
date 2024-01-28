package cz.utb.flashcards.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import cz.utb.flashcards.PreferenceManager
import cz.utb.flashcards.api.FlashcardsAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FlashCardViewModel : ViewModel() {


    private var _flashCards: List<FlashcardModel> = emptyList()
    val items: List<FlashcardModel> get() = _flashCards

    suspend fun fetchItems(context: Context, categoryName: String): List<FlashcardModel> {
        Log.d(
            "FlashCardViewModel",
            "fetchItems: Fetching items from API for category ${categoryName}"
        )
        withContext(Dispatchers.IO) {
            val baseUrl = "https://example.com/"
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(FlashcardsAPIService::class.java)
            val response = service.getSpreadsheetData(categoryName).execute()

            if (response.isSuccessful) {
                Log.d("FlashCardViewModel", "fetchItems: API request successful ${response.body()}")
                val spreadsheetResponse = response.body()
                if (spreadsheetResponse != null) {
                    // Transform SpreadsheetResponse to FlashcardModel
                    val flashcardModels = transformToFlashcardModels(context, spreadsheetResponse)
                    Log.d("FlashCardViewModel", "flashcardModels:  ${flashcardModels}")

                    // Update _flashCards list with FlashcardModel objects
                    _flashCards = flashcardModels

                    Log.d("FlashCardViewModel", "flashcardModels items:  ${items}")
                } else {
                    Log.e(
                        "FlashCardViewModel",
                        "fetchItems: API request 2 failed with code ${response.code()} , ${baseUrl}"
                    )
                }
            } else {
                Log.e(
                    "FlashCardViewModel",
                    "fetchItems: API request failed with code ${response.code()}, ${baseUrl}"
                )
            }
        }

        return _flashCards
    }

    fun transformToFlashcardModels(
        context: Context,
        spreadsheetResponse: SpreadsheetResponse
    ): List<FlashcardModel> {


        val preferenceManager = PreferenceManager(context)
        val isCzechToEnglish = preferenceManager.isCzechToEnglish
        Log.d("preference", " ${isCzechToEnglish}")


        val flashcardModels = mutableListOf<FlashcardModel>()
        val values = spreadsheetResponse.values

        for (row in values) {
            if (row.size >= 2) { // Ensure the row has at least two elements (English and Czech translations)
                val englishWord = if (isCzechToEnglish) row[0] else row[1]
                val czechTranslation = if (isCzechToEnglish) row[1] else row[0]
                val imageURL = if (row.size >= 3) row[2] else null
                val flashcardModel = FlashcardModel(englishWord, czechTranslation, imageURL)
                flashcardModels.add(flashcardModel)
            }
        }

        return flashcardModels
    }
}