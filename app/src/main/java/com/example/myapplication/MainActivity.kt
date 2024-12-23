package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Buttons(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Composable
fun Buttons(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {

        Spacer(modifier = Modifier.height(8.dp)) // Add spacing between elements
        Button(onClick = {
            val database = Firebase.database("https://mobile-programming-class-default-rtdb.europe-west1.firebasedatabase.app")
            val myRef = database.getReference("message")

            myRef.setValue("Hello, World!")

        /* Perform action */ }) {
            Text(text = "Click Me")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* Perform action */
            val database = Firebase.database("https://mobile-programming-class-default-rtdb.europe-west1.firebasedatabase.app")
            val myRef = database.getReference("message")
            myRef.addValueEventListener(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = snapshot.getValue<String>()
                    Log.d(TAG, "Value is: " + value)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }

            })



        }) {
            Text(text = "Click Me")
        }
        var showDialog by remember { mutableStateOf(false) }
        Button(onClick = { /* Perform action */
            showDialog = true
        }) {
            Text(text = "Click Me")
        }
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
                            text = "Dialog Title",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text("This is the content of the dialog.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showDialog = false }) {
                            Text(text = "Close")
                        }
                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        //Greeting("Android")
        Buttons("Android")
    }
}





