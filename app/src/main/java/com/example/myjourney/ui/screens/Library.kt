package com.example.myjourney.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.data.local.LocalLibraryManager
import com.example.myjourney.ui.Navigation.Screen
import com.example.myjourney.ui.components.AddFAB
import com.example.myjourney.ui.components.BottomNavigationBar
import com.example.myjourney.ui.components.LibraryCard
import com.example.myjourney.ui.components.MediaItem
import com.example.myjourney.viewmodel.JournalViewModel
import com.example.myjourney.viewmodel.JournalsState
import com.example.myjourney.viewmodel.ViewModelFactory

@Composable
fun LibraryScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as MyJourneyApplication
    val journalViewModel: JournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))
    val journalsState by journalViewModel.journalsState.collectAsState()

    val localLibraryManager = remember { LocalLibraryManager(context) }
    
    // Dynamically retrieve real-time counts
    val draftsCount = localLibraryManager.getDrafts().size
    val deletedCount = localLibraryManager.getRecentlyDeleted().size
    val albumsCount = when (val state = journalsState) {
        is JournalsState.Success -> state.journals.count { !it.photos.isNullOrEmpty() || it.coverImageUrl != null }
        else -> 0
    }

    val scrollState = rememberScrollState()

    // Trigger fresh load on enter
    LaunchedEffect(Unit) {
        journalViewModel.fetchJournals()
    }

    Scaffold(
        floatingActionButton = {
            AddFAB(navController = navController)
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your saved memories",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== First Row =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LibraryCard(
                    icon = Icons.Default.Description,
                    title = "Drafts",
                    count = if (draftsCount == 1) "1 item" else "$draftsCount items",
                    onClick = { navController.navigate("${Screen.LibraryList.route}/drafts") },
                    modifier = Modifier.weight(1f)
                )

                LibraryCard(
                    icon = Icons.Default.Collections,
                    title = "Albums",
                    count = if (albumsCount == 1) "1 item" else "$albumsCount items",
                    onClick = { navController.navigate("${Screen.LibraryList.route}/albums") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Second Row =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LibraryCard(
                    icon = Icons.Default.Delete,
                    title = "Recently Deleted",
                    count = if (deletedCount == 1) "1 item" else "$deletedCount items",
                    onClick = { navController.navigate("${Screen.LibraryList.route}/deleted") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.weight(1f)) // empty space
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.all_media_images),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                if (journalsState is JournalsState.Success) {
                    val mediaEntries = (journalsState as JournalsState.Success).journals.filter { it.coverImageUrl != null }
                    items(mediaEntries.size) { index ->
                        val entry = mediaEntries[index]
                        com.example.myjourney.ui.components.MediaItem(
                            imageRes = -1, // Not using local res
                            imageUrl = entry.coverImageUrl,
                            modifier = Modifier.clickable {
                                navController.navigate("${Screen.JournalDetail.route}/${entry.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    LibraryScreen(
        navController = androidx.navigation.compose.rememberNavController()
    )
}