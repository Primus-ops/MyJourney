package com.example.myjourney.ui.screens

import android.R.attr.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myjourney.R

@Composable
fun CreateScreen (navController: NavController) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_to_home))
                }
                //Title of the screen "Create"
                Text(text = stringResource(R.string.create_a_journal), style = MaterialTheme.typography.titleMedium)

                //Save button
                Button(onClick = {}) {
                    Text(text = stringResource(R.string.save_the_journal))
                }
            }
        }
) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Cover Photo", style = MaterialTheme.typography.labelLarge)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text("Upload Cover image", color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

                //Title outlined field to Add the title to the entry
            Text("Title", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter title")},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Subtitle", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter the subtitle")},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Description", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter description")},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        CreateScreen(navController)
    }
}