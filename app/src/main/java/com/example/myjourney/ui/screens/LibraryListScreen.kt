package com.example.myjourney.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.data.local.LocalLibraryManager
import com.example.myjourney.model.JournalEntry
import com.example.myjourney.ui.Navigation.Screen
import com.example.myjourney.ui.components.JournalCard
import com.example.myjourney.viewmodel.CreateJournalState
import com.example.myjourney.viewmodel.CreateJournalViewModel
import com.example.myjourney.viewmodel.JournalViewModel
import com.example.myjourney.viewmodel.JournalsState
import com.example.myjourney.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryListScreen(
    type: String,
    navController: NavController
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyJourneyApplication
    
    // ViewModels
    val journalViewModel: JournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))
    val createViewModel: CreateJournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))
    
    val journalsState by journalViewModel.journalsState.collectAsState()
    val createState by createViewModel.createState.collectAsState()

    val localLibraryManager = remember { LocalLibraryManager(context) }

    // Screen title and list initialization
    val screenTitle = when (type) {
        "drafts" -> "My Drafts"
        "albums" -> "Photo Albums"
        "deleted" -> "Recently Deleted"
        else -> "Library List"
    }

    var localList by remember { mutableStateOf(emptyList<JournalEntry>()) }
    var selectedEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Load data dynamically
    LaunchedEffect(type, journalsState, createState) {
        when (type) {
            "drafts" -> {
                localList = localLibraryManager.getDrafts()
            }
            "deleted" -> {
                localList = localLibraryManager.getRecentlyDeleted()
            }
            "albums" -> {
                journalViewModel.fetchJournals()
            }
        }
    }

    // Handle background actions
    LaunchedEffect(createState) {
        if (createState is CreateJournalState.Success) {
            createViewModel.resetState()
            selectedEntry?.let { entry ->
                if (type == "drafts") {
                    localLibraryManager.deleteDraft(entry.id ?: 0)
                    localList = localLibraryManager.getDrafts()
                } else if (type == "deleted") {
                    localLibraryManager.removeRecentlyDeleted(entry.id ?: 0)
                    localList = localLibraryManager.getRecentlyDeleted()
                }
            }
            Toast.makeText(context, "Memory published successfully!", Toast.LENGTH_SHORT).show()
            showDialog = false
            selectedEntry = null
            journalViewModel.fetchJournals()
        } else if (createState is CreateJournalState.Error) {
            val errorMsg = (createState as CreateJournalState.Error).message
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            createViewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (type == "albums") {
                when (val state = journalsState) {
                    is JournalsState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is JournalsState.Error -> {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    is JournalsState.Success -> {
                        val albumEntries = state.journals.filter { !it.photos.isNullOrEmpty() || it.coverImageUrl != null }
                        if (albumEntries.isEmpty()) {
                            EmptyState(message = "No photo memories found. Upload a cover photo to see it here!")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(albumEntries) { entry ->
                                    JournalCard(
                                        JournalEntry = entry,
                                        navController = navController,
                                        onFavoriteClick = { journalViewModel.toggleFavorite(it) },
                                        onDeleteClick = { journalViewModel.deleteJournal(it, context) }
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }
            } else {
                // Drafts or Deleted local entries
                if (localList.isEmpty()) {
                    val emptyMessage = if (type == "drafts") {
                        "No drafts found. Tap 'Draft' in the creation screen to save one!"
                    } else {
                        "Your trash is clean. No recently deleted memories!"
                    }
                    EmptyState(message = emptyMessage)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(localList) { entry ->
                            // Custom clean clickable card layout for local drafts/trash
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedEntry = entry
                                        showDialog = true
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = entry.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = entry.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = entry.displayDate ?: entry.entryDate,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Options modal dialog for drafts/recently deleted
            if (showDialog && selectedEntry != null) {
                val entry = selectedEntry!!
                AlertDialog(
                    onDismissRequest = { 
                        if (createState !is CreateJournalState.Loading) {
                            showDialog = false 
                        }
                    },
                    title = { Text(if (type == "drafts") "Draft Options" else "Recently Deleted") },
                    text = { 
                        Text(
                            if (type == "drafts") {
                                "Would you like to publish this draft to your journal on the server, or delete it forever?"
                            } else {
                                "Would you like to restore this deleted memory to your live dashboard, or permanently remove it?"
                            }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (type == "drafts") {
                                    createViewModel.createJournal(entry.title, entry.content, null)
                                } else {
                                    createViewModel.createJournal(entry.title, entry.content, null)
                                }
                            },
                            enabled = createState !is CreateJournalState.Loading
                        ) {
                            if (createState is CreateJournalState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text(if (type == "drafts") "Publish" else "Restore")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                if (type == "drafts") {
                                    localLibraryManager.deleteDraft(entry.id ?: 0)
                                    localList = localLibraryManager.getDrafts()
                                } else {
                                    localLibraryManager.removeRecentlyDeleted(entry.id ?: 0)
                                    localList = localLibraryManager.getRecentlyDeleted()
                                }
                                showDialog = false
                                selectedEntry = null
                                Toast.makeText(context, "Removed permanently.", Toast.LENGTH_SHORT).show()
                            },
                            enabled = createState !is CreateJournalState.Loading,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete Forever")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📂",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
