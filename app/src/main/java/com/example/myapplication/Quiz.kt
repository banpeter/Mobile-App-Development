package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



@Composable
fun WordApp2(database: FirebaseDatabase) {
    var randomWords by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    var userAnswers by remember { mutableStateOf(mutableListOf<String>()) }
    var showQuiz by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            fetchWordPairs2(database) { words ->
                randomWords = words.shuffled().take(5) // Select 5 random pairs
                userAnswers = MutableList(randomWords.size) { "" } // Initialize answers
                showQuiz = true
            }
        }) {
            Text("Start Quiz")
        }

        Spacer(modifier = Modifier.height(16.dp))

//        if (showQuiz) {
//            Quiz(randomWords, userAnswers) { updatedAnswers ->
//                userAnswers = updatedAnswers.toMutableList()
//            }
//        }
    }
}

@Composable
fun Quiz(
    randomWords: List<Pair<String, String>>,
    userAnswers: List<String>,
    onAnswersChanged: (List<String>) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        randomWords.forEachIndexed { index, pair ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Translate: ${pair.first}",
                    style = MaterialTheme.typography.bodyLarge
                )
                TextField(
                    value = userAnswers[index],
                    onValueChange = { answer ->
                        val updatedAnswers = userAnswers.toMutableList()
                        updatedAnswers[index] = answer
                        onAnswersChanged(updatedAnswers)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Answer") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val results = randomWords.mapIndexed { index, pair ->
                userAnswers[index] == pair.second // Check correctness
            }
            val correctCount = results.count { it }
            Log.d("QuizResults", "You got $correctCount out of ${randomWords.size} correct!")
        }) {
            Text("Submit Answers")
        }
    }
}


fun fetchWordPairs2(
    database: FirebaseDatabase,
    onResult: (List<Pair<String, String>>) -> Unit
) {
    val databaseReference = database.getReference("wordPairs")

    databaseReference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val words = mutableListOf<Pair<String, String>>()
            for (data in snapshot.children) {
                val word1 = data.child("word1").getValue(String::class.java) ?: ""
                val word2 = data.child("word2").getValue(String::class.java) ?: ""
                words.add(Pair(word1, word2))
            }
            onResult(words)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Failed to fetch word pairs", error.toException())
        }
    })
}