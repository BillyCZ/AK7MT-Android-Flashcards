package cz.utb.flashcards

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.utb.flashcards.ui.theme.FlashcardsTheme
import androidx.compose.foundation.layout.Column
import cz.utb.flashcards.model.FlashCardViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text

import androidx.lifecycle.viewmodel.compose.viewModel
import cz.utb.flashcards.model.FlashcardModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowFlashCardsScreen()
                }
            }
        }
    }
}


@Composable
fun ShowFlashCardsScreen(viewModel: FlashCardViewModel = viewModel()) {
    // Use remember to hold the state of the items
    val itemsState = remember { mutableStateOf<List<FlashcardModel>>(emptyList()) }

    // Trigger the data fetching when the composable is first launched
    LaunchedEffect(Unit) {
        // Call fetchItems and update the state with the result
        val items = viewModel.fetchItems()
        itemsState.value = items
    }

    // Render the UI based on the state of the items
    Column {
        Text(text = "List of Items:")
        LazyColumn {
            itemsState.value.forEach { flashcard ->
                item {
                    Text(text = "English: ${flashcard.english}, Czech: ${flashcard.czech}")
                }
            }
        }
    }
}