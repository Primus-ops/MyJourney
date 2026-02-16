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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myjourney.R
import com.example.myjourney.model.JournalEntry
import com.example.myjourney.ui.components.AddFAB
import com.example.myjourney.ui.components.BottomNavigationBar
import com.example.myjourney.ui.components.JournalCard
import com.example.myjourney.ui.components.SearchBar


@SuppressLint("ResourceType")
@Composable
fun HomeScreen(navController: NavController) {

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

        LazyColumn( modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                //Title
                Text(
                    text = stringResource(R.string.my_journey),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

                //Search Bar (component)
                item{
                    SearchBar()
                }

                //Section title
                item{
                    Text(
                        text = stringResource(R.string.my_stories),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                //Journal Entries {
                item {
                    JournalCard(
                        JournalEntry = JournalEntry(
                            imageResId = R.drawable.image1,
                            title = R.string.title,
                            subtitle = R.string.subtitle,
                            description = R.string.description,
                            year = R.string.year,
                            ),
                            navController = navController
                    )
                }

                    item {
                        JournalCard(
                            JournalEntry = JournalEntry(
                                imageResId = R.drawable.image2,
                                title = R.string.title2,
                                subtitle = R.string.subtitle2,
                                description = R.string.description2,
                                year = R.string.year2
                            ),
                            navController = navController
                        )
                    }

                    item {
                        JournalCard(
                            JournalEntry = JournalEntry(
                                imageResId = R.drawable.image3,
                                title = R.string.title3,
                                subtitle = R.string.subtitle3,
                                description = R.string.description3,
                                year = R.string.year3
                            ),
                            navController = navController
                        )
                    }

                    item {
                        JournalCard(
                            JournalEntry = JournalEntry(
                                imageResId = R.drawable.image4,
                                title = R.string.title4,
                                subtitle = R.string.subtitle4,
                                description = R.string.description4,
                                year = R.string.year4
                            ),
                            navController = navController
                        )
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



