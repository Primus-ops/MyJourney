package com.example.myjourney.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.model.JournalEntry
import com.example.myjourney.ui.components.AddFAB
import com.example.myjourney.ui.components.BottomNavigationBar
import com.example.myjourney.ui.components.JournalCard
import com.example.myjourney.ui.components.SearchBar
import com.example.myjourney.viewmodel.JournalViewModel
import com.example.myjourney.viewmodel.JournalsState
import com.example.myjourney.viewmodel.ViewModelFactory

/**
 * HomeScreen
 * 
 * The primary dashboard screen of the application. Built using Jetpack Compose declarative UI.
 * It dynamically observes the state of JournalViewModel and handles redrawing of components
 * reactively depending on whether the remote API is Loading, Success, or Error.
 */
@SuppressLint("ResourceType")
@Composable
fun HomeScreen(navController: NavController) {
    // Obtain application and platform contexts
    val context = LocalContext.current
    val app = context.applicationContext as MyJourneyApplication
    
    // Inject dynamic, lifecycle-aware JournalViewModel powered by custom ViewModelFactory
    val viewModel: JournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))
    
    // Convert StateFlow values into Compose States to trigger auto-recomposition
    val journalsState by viewModel.journalsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // NEW: Ensure we fetch with context so filtering works on launch
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.fetchJournals(context)
    }

    // Scaffold manages standard material layouts like custom floating actions and bottom bars
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            AddFAB(
                navController = navController
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->

        // LazyColumn acts like a modern RecyclerView, loading and recycling elements efficiently
        LazyColumn( modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Item 1: App Header Title Block
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.my_journey),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            // Item 2: Interactive search field header component
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) }
                )
            }

            // Item 3: Secondary Section Title
            item{
                Text(
                    text = stringResource(R.string.my_stories),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Item 4: Asynchronous State Renderer
            when (val state = journalsState) {
                
                // State A: Loading - Draw a sleek Material circular indicator
                is JournalsState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                
                // State B: Network/Auth Error - Print raw error in Material Error red theme
                is JournalsState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                
                // State C: API Success - Stream list elements to custom card renderers
                is JournalsState.Success -> {
                    val filteredJournals = state.journals.filter { 
                        it.title.contains(searchQuery, ignoreCase = true) || 
                        it.content.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredJournals.size) { index ->
                        JournalCard(
                            JournalEntry = filteredJournals[index],
                            navController = navController,
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onDeleteClick = { viewModel.deleteJournal(it, context) }
                        )
                    }
                    
                    if (filteredJournals.isEmpty()) {
                        item {
                            Text(
                                text = "No journals found for \"$searchQuery\"",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                else -> {} // Ignore single journal detail loading states
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navController = androidx.navigation.compose.rememberNavController()
    )
}
