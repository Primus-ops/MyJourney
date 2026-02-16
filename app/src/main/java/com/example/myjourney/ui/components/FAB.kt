package com.example.myjourney.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.myjourney.ui.theme.MyJourneyTheme

@Composable
fun AddFAB(navController: NavController) {
    FloatingActionButton(onClick = { navController.navigate("create_journal") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary) {
        Icon(Icons.Default.Add, contentDescription = "Add")
    }
}

