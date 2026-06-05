package com.example.myjourney.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.model.JournalEntry
import com.example.myjourney.ui.theme.MyJourneyTheme
import com.example.myjourney.viewmodel.JournalViewModel
import com.example.myjourney.viewmodel.JournalsState
import com.example.myjourney.viewmodel.ViewModelFactory

@SuppressLint("ResourceType")
@Composable
fun JournalDetail(
    entryId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyJourneyApplication
    val viewModel: JournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))
    
    val state by viewModel.singleJournalState.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch the journal when the screen opens
    LaunchedEffect(entryId) {
        viewModel.fetchJournalById(entryId)
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

                if (state is JournalsState.SingleSuccess) {
                    val entry = (state as JournalsState.SingleSuccess).journal
                    Text(
                        text = entry.displayDate ?: entry.entryDate,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Button(onClick = { /*Edit*/ }) {
                    Text(text = stringResource(R.string.edit_journal))
                }
            }
        }
    ) { paddingValues ->
        when (val s = state) {
            is JournalsState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is JournalsState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is JournalsState.SingleSuccess -> {
                val journalEntry = s.journal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                ) {
                    Box {
                        if (journalEntry.coverImageUrl != null) {
                            AsyncImage(
                                model = journalEntry.coverImageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            )
                        } else if (journalEntry.imageResId != null) {
                            Image(
                                painter = painterResource(journalEntry.imageResId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = journalEntry.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = journalEntry.content,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Events", style = MaterialTheme.typography.titleMedium)
                        Text(text = "No events yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JournalDetailPreview() {
    MyJourneyTheme {
        // Preview can't fetch from API, so this will show Loading
        JournalDetail(entryId = 1, navController = rememberNavController())
    }
}
