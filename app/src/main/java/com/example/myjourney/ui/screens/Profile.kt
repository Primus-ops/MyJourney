package com.example.myjourney.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.ui.Navigation.Screen
import com.example.myjourney.ui.components.BottomNavigationBar
import com.example.myjourney.viewmodel.ProfileState
import com.example.myjourney.viewmodel.ProfileViewModel
import com.example.myjourney.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    
    // Triple-Layer Safety: Safely extract the application context
    val app = remember(context) { 
        context.applicationContext as? MyJourneyApplication 
    }

    if (app == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("System Error: Critical Context Missing", color = Color.Red)
        }
        return
    }

    // Initialize ViewModel with a clean factory
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory(app.tokenManager)
    )
    val uiState by profileViewModel.uiState.collectAsState()

    // Trigger data loading ONLY after screen has stabilized
    LaunchedEffect(Unit) {
        delay(200)
        profileViewModel.fetchUserProfile()
    }

    // Handle session termination
    LaunchedEffect(uiState) {
        if (uiState is ProfileState.LogoutSuccess) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.Profile_header),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { profileViewModel.fetchUserProfile() }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Identity Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        val user = (uiState as? ProfileState.Success)?.user
                        val photoUrl = user?.profilePhotoUrl ?: user?.userWrapper?.profilePhotoUrl ?: user?.dataWrapper?.profilePhotoUrl
                        
                        if (photoUrl != null) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val state = uiState) {
                        is ProfileState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                        is ProfileState.Success -> {
                            val user = state.user
                            // Fallback logic: check root -> check .user -> check .data
                            val finalName = user?.name ?: user?.userWrapper?.name ?: user?.dataWrapper?.name ?: "Unknown User"
                            val finalEmail = user?.email ?: user?.userWrapper?.email ?: user?.dataWrapper?.email ?: "No Email Found"
                            
                            Text(text = finalName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(text = finalEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        is ProfileState.Error -> {
                            Text("Profile Error", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            Text(state.message, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            TextButton(onClick = { profileViewModel.fetchUserProfile() }) {
                                Text("Retry")
                            }
                        }
                        else -> {
                            Text("Connecting...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Settings Section
            Text("Settings", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            ListItem(
                headlineContent = { Text("Dark Mode") },
                supportingContent = { Text("Switch theme appearance") },
                trailingContent = {
                    Switch(checked = isDarkMode, onCheckedChange = onDarkModeChange)
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Logout Button
            Button(
                onClick = { profileViewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Log Out", fontWeight = FontWeight.Bold)
            }
        }
    }
}
