package com.example.myjourney.ui.screens

import com.example.myjourney.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myjourney.ui.components.AddFAB
import com.example.myjourney.ui.components.BottomNavigationBar
import com.example.myjourney.ui.components.LibraryCard
import com.example.myjourney.ui.components.MediaItem

@Composable
fun LibraryScreen(navController: NavController) {

    val scrollState = rememberScrollState()

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
                    count = "3 items",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                LibraryCard(
                    icon = Icons.Default.Collections,
                    title = "Albums",
                    count = "5 items",
                    onClick = {},
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
                    count = "2 items",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.weight(1f)) // empty space (since Loved removed)
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
                    .height(300.dp)
            ) {
                item {
                    MediaItem(
                        imageRes = R.drawable.image1,
                        modifier = Modifier
                    )
                }
                item {
                    MediaItem(
                        imageRes = R.drawable.image2,
                        modifier = Modifier
                    )
                }
                item {
                    MediaItem(
                        imageRes = R.drawable.image3,
                        modifier = Modifier
                    )
                }
                item {
                    MediaItem(
                        imageRes = R.drawable.image4,
                        modifier = Modifier
                    )
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