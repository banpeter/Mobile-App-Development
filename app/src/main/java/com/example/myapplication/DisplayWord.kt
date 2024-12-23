package com.example.myapplication

import android.util.Log
import androidx.compose.ui.window.Dialog

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
fun WordApp(database: FirebaseDatabase) {
    var showWords by remember { mutableStateOf(false) }
    val wordPairs = remember { mutableStateListOf<Pair<String, String>>() }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = {
            // Fetch words and update the list
            fetchWordPairs(database) { words ->
                wordPairs.clear()
                wordPairs.addAll(words)
                showWords = true
            }
        }) {
            Text("Display Word Pairs")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showWords) {
            WordList(wordPairs = wordPairs)
        }
    }
}


@Composable
fun WordList(wordPairs: List<Pair<String, String>>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Use items from androidx.compose.foundation.lazy.LazyColumn
        items(wordPairs) { pair ->
            WordItem(pair)
        }
    }
}

@Composable
fun WordItem(pair: Pair<String, String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),


    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = pair.first, style = MaterialTheme.typography.bodyLarge)
            Text(text = pair.second, style = MaterialTheme.typography.bodyLarge)
        }
    }
}




fun fetchWordPairs(
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
