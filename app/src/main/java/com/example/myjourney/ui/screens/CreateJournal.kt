package com.example.myjourney.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myjourney.MyJourneyApplication
import com.example.myjourney.R
import com.example.myjourney.ui.Navigation.Screen
import com.example.myjourney.viewmodel.CreateJournalState
import com.example.myjourney.viewmodel.CreateJournalViewModel
import com.example.myjourney.viewmodel.ViewModelFactory
import java.io.File
import java.util.*

@Composable
fun CreateScreen(
    navController: NavController,
    appDarkMode: Boolean
) {
    val context = LocalContext.current
    val app = context.applicationContext as MyJourneyApplication
    val viewModel: CreateJournalViewModel = viewModel(factory = ViewModelFactory(app.tokenManager))

    val createState by viewModel.createState.collectAsState()
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var locationName by remember { mutableStateOf("Fetching location...") }
    var ambientMood by remember { mutableStateOf("Detecting mood...") }

    val locationHelper = remember { com.example.myjourney.utils.LocationHelper(context) }
    val moodManager = remember { com.example.myjourney.utils.AmbientMoodManager(context) }
    val privacyManager = remember { com.example.myjourney.utils.PrivacyFlipManager(context) }

    DisposableEffect(Unit) {
        locationHelper.getCurrentLocation { result ->
            locationName = result
        }
        moodManager.start { mood, _ ->
            ambientMood = mood
        }
        privacyManager.start {
            // PANIC SHIELD TRIGGERED! 🛡️🤫
            if (title.isNotBlank() || content.isNotBlank()) {
                val localLibraryManager = com.example.myjourney.data.local.LocalLibraryManager(context)
                localLibraryManager.saveDraft(title, content)
            }
            navController.popBackStack()
        }
        onDispose {
            moodManager.stop()
            privacyManager.stop()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(createState) {
        if (createState is CreateJournalState.Success) {
            viewModel.resetState()
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(Screen.HomeScreen.route) { inclusive = true }
            }
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
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_to_home)
                    )
                }

                Text(text = "New Memory", style = MaterialTheme.typography.titleMedium)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                val localLibraryManager =
                                    com.example.myjourney.data.local.LocalLibraryManager(context)
                                localLibraryManager.saveDraft(title, content)
                                android.widget.Toast.makeText(
                                    context,
                                    "Draft saved locally!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
                                android.widget.Toast.makeText(
                                    context,
                                    "Cannot save an empty draft.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = createState !is CreateJournalState.Loading
                    ) {
                        Text(text = "Draft", color = MaterialTheme.colorScheme.primary)
                    }

                    Button(
                        onClick = {
                            val file = imageUri?.let { getFileFromUri(context, it) }

                            val footer = buildString {
                                if (locationName != "Fetching location..." && locationName != "Location unavailable") {
                                    append("\n\n📍 Written in $locationName")
                                }
                                if (ambientMood != "Detecting mood..." && ambientMood != "Mood sensor unavailable") {
                                    append("\n📜 Ambience: $ambientMood")
                                }
                            }

                            val finalContent = "$content$footer"
                            viewModel.createJournal(title, finalContent, file)
                        },
                        enabled = createState !is CreateJournalState.Loading
                    ) {
                        if (createState is CreateJournalState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Save")
                        }
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
            if (createState is CreateJournalState.Error) {
                Text(
                    text = (createState as CreateJournalState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text("Cover Photo", style = MaterialTheme.typography.labelLarge)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { launcher.launch("image/*") },
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Cover Photo Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Select Cover Image from Gallery",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Text(
                text = "📍 $locationName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "✨ Ambience: $ambientMood",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Text("Title", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Enter title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Text("Description", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("What happened today?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

/**
 * Helper function to copy the selected gallery image URI into a temporary File
 * for Retrofit multipart upload.
 */
private fun getFileFromUri(context: Context, uri: Uri): File {
    val tempFile = File(context.cacheDir, "temp_journal_photo_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return tempFile
}