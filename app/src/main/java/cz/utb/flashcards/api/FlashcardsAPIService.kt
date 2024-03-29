package cz.utb.flashcards.api

import cz.utb.flashcards.model.SpreadsheetResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call

interface FlashcardsAPIService {

@GET("https://sheets.googleapis.com/v4/spreadsheets/1HzjUzlPmlkbutyVspYG67c9_hRZ3GfhSuciePnXgdo8/values/{category}?alt=json&key=AIzaSyBoWIrpaixBl7QtNFchnkjuUAcDFVWjUpE")
    fun getSpreadsheetData(@Path("category") category: String): Call<SpreadsheetResponse>

}