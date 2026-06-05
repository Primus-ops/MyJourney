package com.example.myjourney.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.model.JournalEntry
import com.example.myjourney.ui.components.AddFAB
import com.example.myjourney.ui.components.BottomNavigationBar
import com.example.myjourney.ui.components.JournalCard
import com.example.myjourney.viewmodel.JournalViewModel
import com.example.myjourney.viewmodel.JournalsState
import com.example.myjourney.viewmodel.ViewModelFactory
import com.example.myjourney.ui.theme.MyJourneyTheme
import com.example.myjourney.ui.Navigation.Screen

@SuppressLint("ResourceType")
@Composable
fun FavoriteScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as MyJourneyApplication
    val viewModel: JournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))
    
    val journalsState by viewModel.journalsState.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        floatingActionButton = {
            AddFAB(navController = navController)
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            //Journal Entries (Filtered for Favorites)
            when (val state = journalsState) {
                is JournalsState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is JournalsState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is JournalsState.Success -> {
                    val favorites = state.journals.filter { it.isFavorite }
                    items(favorites.size) { index ->
                        JournalCard(
                            JournalEntry = favorites[index],
                            navController = navController,
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onDeleteClick = { viewModel.deleteJournal(it, context) }
                        )
                    }
                    
                    if (favorites.isEmpty()) {
                        item {
                            Text(
                                text = "You haven't favorited any memories yet.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}