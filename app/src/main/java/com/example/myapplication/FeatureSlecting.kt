package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



@Composable
fun WordApp3(database: FirebaseDatabase) {
    var currentScreen by remember { mutableStateOf("home") } // Tracks current screen
    var randomWords by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    var userAnswers by remember { mutableStateOf(mutableListOf<String>()) }

    when (currentScreen) {
        "home" -> {
            // Display the home screen with buttons
            HomeScreen(
                onShowWords = {
                    currentScreen = "wordList"
                },
                onStartQuiz = {
                    fetchWordPairs(database) { words ->
                        randomWords = words.shuffled().take(5)
                        userAnswers = MutableList(randomWords.size) { "" }
                        currentScreen = "quiz"
                    }
                }
            )
        }
        "wordList" -> {
            // Display the word list
            WordListScreen(database, onBack = { currentScreen = "home" })
        }
        "quiz" -> {
            // Display the quiz
            QuizScreen(
                randomWords = randomWords,
                userAnswers = userAnswers,
                onAnswersChanged = { updatedAnswers -> userAnswers =
                    updatedAnswers.toMutableList()
                },
                onBack = { currentScreen = "home" }
            )
        }
    }
}


@Composable
fun HomeScreen(onShowWords: () -> Unit, onStartQuiz: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onShowWords, modifier = Modifier.fillMaxWidth()) {
            Text("Show Word List")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onStartQuiz, modifier = Modifier.fillMaxWidth()) {
            Text("Start Quiz")
        }
    }
}


@Composable
fun WordListScreen(database: FirebaseDatabase, onBack: () -> Unit) {
    var wordPairs by remember { mutableStateOf(emptyList<Pair<String, String>>()) }

    // Fetch words when the screen loads
    LaunchedEffect(Unit) {
        fetchWordPairs(database) { words -> wordPairs = words }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(wordPairs) { pair ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(pair.first, style = MaterialTheme.typography.bodyLarge)
                        Text(pair.second, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}


@Composable
fun QuizScreen(
    randomWords: List<Pair<String, String>>,
    userAnswers: List<String>,
    onAnswersChanged: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var correctAnswers by remember { mutableStateOf(0) }
    var showResultDialog by remember { mutableStateOf(false) }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("Quiz Results") },
            text = { Text("You got $correctAnswers out of ${randomWords.size} correct!") },
            confirmButton = {
                Button(onClick = {
                    showResultDialog = false
                    onBack()
                }) {
                    Text("OK")
                }
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Translate: ${randomWords[currentQuestionIndex].first}",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = userAnswers[currentQuestionIndex],
                onValueChange = { answer ->
                    val updatedAnswers = userAnswers.toMutableList()
                    updatedAnswers[currentQuestionIndex] = answer
                    onAnswersChanged(updatedAnswers)
                },
                label = { Text("Your Answer") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (userAnswers[currentQuestionIndex].trim()
                            .equals(randomWords[currentQuestionIndex].second, ignoreCase = true)
                    ) {
                        correctAnswers++
                    }

                    if (currentQuestionIndex < randomWords.size - 1) {
                        currentQuestionIndex++
                    } else {
                        showResultDialog = true
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (currentQuestionIndex < randomWords.size - 1) "Next" else "Submit")
            }
        }
    }
}


//@Composable
//fun QuizScreen(
//    randomWords: List<Pair<String, String>>,
//    userAnswers: List<String>,
//    onAnswersChanged: (List<String>) -> Unit,
//    onBack: () -> Unit
//) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
//            Text("Back to Home")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        randomWords.forEachIndexed { index, pair ->
//            Column(modifier = Modifier.padding(8.dp)) {
//                Text(
//                    text = "Translate: ${pair.first}",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                TextField(
//                    value = userAnswers[index],
//                    onValueChange = { answer ->
//                        val updatedAnswers = userAnswers.toMutableList()
//                        updatedAnswers[index] = answer
//                        onAnswersChanged(updatedAnswers)
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    label = { Text("Your Answer") }
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//            val results = randomWords.mapIndexed { index, pair ->
//                userAnswers[index] == pair.second
//            }
//            val correctCount = results.count { it }
//            Log.d("QuizResults", "You got $correctCount out of ${randomWords.size} correct!")
//        }) {
//            Text("Submit Answers")
//        }
//    }
//}
