package cz.utb.flashcards


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import cz.utb.flashcards.model.FlashCardViewModel
import cz.utb.flashcards.model.FlashcardModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            NavHost(navController, startDestination = "buttonScreen") {
                composable("buttonScreen") {
                    ButtonScreen(navController = navController, context = context)
                }
                composable(
                    route = "ShowFlashCardsScreen/{category}",
                    arguments = listOf(navArgument("category") { type = NavType.StringType })
                ) { backStackEntry ->
                    val categoryName = backStackEntry.arguments?.getString("category") ?: ""
                    ShowFlashCardsScreen(navController, categoryName, context)
                }
            }
        }
    }
}


@Composable
fun ButtonScreen(navController: NavController, context: Context) {
    val preferenceManager = remember { PreferenceManager(context) }
    var isCzechToEnglish by remember { mutableStateOf(preferenceManager.isCzechToEnglish) }


    Column(

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "FlashCards",
            color = Color.Blue,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )


        Text(
            text = if (isCzechToEnglish) "Czech to English" else "English to Czech",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Toggle button to switch between translation directions
        Switch(
            checked = isCzechToEnglish,
            onCheckedChange = {
                isCzechToEnglish = it
                // Save new translation direction preference
                preferenceManager.isCzechToEnglish = it
            },

            modifier = Modifier.padding(bottom = 16.dp)
        )


    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Select category:")
        // Button to navigate to Fruit category
        Button(onClick = { navController.navigate("ShowFlashCardsScreen/fruits") }) {
            Text(text = "Fruits")
        }
        // Button to navigate to Animals category
        Button(onClick = { navController.navigate("ShowFlashCardsScreen/animals") }) {
            Text(text = "Animals")
        }
        // Button to navigate to Other category
        Button(onClick = { navController.navigate("ShowFlashCardsScreen/other") }) {
            Text(text = "Other")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowFlashCardsScreen(
    navController: NavController,
    categoryName: String,
    context: Context,
    viewModel: FlashCardViewModel = viewModel()
) {
    // Use remember to hold the state of the items
    val itemsState = remember { mutableStateOf<List<FlashcardModel>>(emptyList()) }
    val pagerState = rememberPagerState(pageCount = {
        10
    })
    var showTranslation by remember { mutableStateOf(false) }
    var showHint by remember {
        mutableStateOf(false)
    }


    val rotation by animateFloatAsState(
        targetValue = if (showTranslation) 180f else 0f,
        animationSpec = tween(500)
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!showTranslation) 1f else 0f,
        animationSpec = tween(500)
    )

    val animateBack by animateFloatAsState(
        targetValue = if (showTranslation) 1f else 0f,
        animationSpec = tween(500)
    )

    val animateColor by animateColorAsState(
        targetValue = if (showTranslation) Color.Red else Color.Blue,
        animationSpec = tween(500)
    )

    LaunchedEffect(Unit) {


        val items = viewModel.fetchItems(context, categoryName)
        itemsState.value = items
    }

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->

            //page change
            showTranslation = false;
            showHint = false;
        }
    }

    Column(

    ) {
        Button(
            onClick = { navController.navigate("ButtonScreen") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Back")

        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .paddingFromBaseline(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Category: $categoryName",
            )

        }


        // Use HorizontalPager to display each flashcard
        HorizontalPager(pagerState) { page ->
            val flashcard = itemsState.value.getOrNull(page)
            if (flashcard != null) {


                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val rotationAngle = if (showTranslation) 180f else 0f
                    Card(
                        modifier = Modifier
                            .padding(50.dp)
                            .height(250.dp)
                            .width(200.dp)
                            .clickable(onClick = {
                                //toggle the translation
                                showTranslation = !showTranslation
                                showHint = false
                                Log.d("show Flash Cards", "showTranslation: ${showTranslation}")
                            })
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 8 * density
                            }


                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {

                            Text(
                                text = if (showTranslation) "${flashcard.english}" else "${flashcard.czech}",
                                fontSize = 20.sp,
                                modifier = Modifier.graphicsLayer {
                                    alpha = if (showTranslation) animateBack else animateFront
                                    rotationY = rotation
                                }

                            )

                            Column(
                                Modifier
                                    .height(100.dp)
                                    .padding(24.dp)
                            ) {
                                if (flashcard.imageURL != null) {
                                    AsyncImage(
                                        model = "${flashcard.imageURL}",
                                        contentDescription = ""
                                    )
                                }
                            }

                            Column() {
                                if (showHint && !showTranslation) {
                                    val firstLetter =
                                        flashcard.english.firstOrNull()?.toString() ?: ""
                                    val remainingLength = flashcard.english.length - 1
                                    val underscoreString = "_ ".repeat(remainingLength)
                                    Text(
                                        text = "$firstLetter$underscoreString",
                                        fontSize = 20.sp
                                    )

                                }
                            }


                        }
                    }

                    Button(

                        onClick = { showHint = !showHint },
                        enabled = !showTranslation,
                        modifier = Modifier.padding(16.dp)

                    ) {
                        Text(text = "Hint")

                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Click on the card for translation or swipe for next")
        }

    }
}
