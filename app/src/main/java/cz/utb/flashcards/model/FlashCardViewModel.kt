package cz.utb.flashcards.model
import androidx.lifecycle.ViewModel
import cz.utb.flashcards.api.FlashcardsAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Properties
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log


class FlashCardViewModel : ViewModel() {

    private val properties = Properties()

    init {
//        // Load properties file
//        val propertiesFile = "flashcards.properties"
//        val inputStream = FileInputStream(propertiesFile)
//        properties.load(inputStream)
//        inputStream.close()
    }

    private var _flashCards: List<FlashcardModel> = emptyList()
    val items: List<FlashcardModel> get() = _flashCards

    suspend fun fetchItems(categoryName: String): List<FlashcardModel> {
        Log.d("FlashCardViewModel", "fetchItems: Fetching items from API for category ${categoryName}")
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
                    val flashcardModels = transformToFlashcardModels(spreadsheetResponse)
                    Log.d("FlashCardViewModel", "flashcardModels:  ${flashcardModels}")

                    // Update _flashCards list with FlashcardModel objects
                    _flashCards = flashcardModels

                    Log.d("FlashCardViewModel", "flashcardModels items:  ${items}")
                } else {
                    Log.e("FlashCardViewModel", "fetchItems: API request 2 failed with code ${response.code()} , ${baseUrl}")
                }
            } else {
                Log.e("FlashCardViewModel", "fetchItems: API request failed with code ${response.code()}, ${baseUrl}")
            }
        }

        return _flashCards
    }

    fun transformToFlashcardModels(spreadsheetResponse: SpreadsheetResponse): List<FlashcardModel> {
        val flashcardModels = mutableListOf<FlashcardModel>()
        val values = spreadsheetResponse.values

        for (row in values) {
            if (row.size >= 2) { // Ensure the row has at least two elements (English and Czech translations)
                val englishWord = row[0]
                val czechTranslation = row[1]
                val flashcardModel = FlashcardModel(englishWord, czechTranslation)
                flashcardModels.add(flashcardModel)
            }
        }

        return flashcardModels
    }
}