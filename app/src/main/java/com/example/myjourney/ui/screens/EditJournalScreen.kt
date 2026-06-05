package com.example.myjourney.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.ui.Navigation.Screen
import com.example.myjourney.viewmodel.EditJournalState
import com.example.myjourney.viewmodel.EditJournalViewModel
import com.example.myjourney.viewmodel.ViewModelFactory

@Composable
fun EditJournalScreen(
    journalId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyJourneyApplication
    val viewModel: EditJournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))
    
    val editState by viewModel.editState.collectAsState()
    val journalToEdit by viewModel.journalToEdit.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Load journal data once
    LaunchedEffect(journalId) {
        viewModel.loadJournal(journalId)
    }

    // Sync state when data is loaded
    LaunchedEffect(journalToEdit) {
        journalToEdit?.let {
            title = it.title
            content = it.content
        }
    }

    // Handle success
    LaunchedEffect(editState) {
        if (editState is EditJournalState.Success) {
            navController.popBackStack()
        }
    }

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
                
                Text(text = "Edit Memory", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = { viewModel.updateJournal(journalId, title, content) },
                    enabled = editState !is EditJournalState.Loading
                ) {
                    if (editState is EditJournalState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(text = "Update")
                    }
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
            if (editState is EditJournalState.Error) {
                Text(
                    text = (editState as EditJournalState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text("Title", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Description", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
