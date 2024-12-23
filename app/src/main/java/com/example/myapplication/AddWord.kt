package com.example.myapplication

import androidx.compose.ui.window.Dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

@Composable
fun WordPairScreen(database: FirebaseDatabase) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Language Learning App",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AddWordButton(database = database)
    }
}

@Composable
fun AddWordButton(database: FirebaseDatabase) {
    var showDialog by remember { mutableStateOf(false) }
    var word1 by remember { mutableStateOf("") }
    var word2 by remember { mutableStateOf("") }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Add New Word Pair",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TextField(
                        value = word1,
                        onValueChange = { word1 = it },
                        label = { Text("Word 1") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = word2,
                        onValueChange = { word2 = it },
                        label = { Text("Word 2") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                // Submit to Firebase
                                submitWordPair(database, word1, word2)
                                showDialog = false
                                word1 = ""
                                word2 = ""
                            }
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Add Word Pair")
    }
}

fun submitWordPair(database: FirebaseDatabase, word1: String, word2: String) {
    val wordPair = mapOf("word1" to word1, "word2" to word2)
    val databaseReference = database.getReference("wordPairs")
    databaseReference.push().setValue(wordPair)
        .addOnSuccessListener {
            android.util.Log.d("Firebase", "Word pair added successfully!")
        }
        .addOnFailureListener { exception ->
            android.util.Log.e("Firebase", "Failed to add word pair", exception)
        }
}
