package cz.utb.flashcards

import android.content.Context

class PreferenceManager(private val context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "translation_preferences"
        private const val KEY_IS_CZECH_TO_ENGLISH = "is_czech_to_english"
    }

    var isCzechToEnglish: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_CZECH_TO_ENGLISH, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_CZECH_TO_ENGLISH, value).apply()
}