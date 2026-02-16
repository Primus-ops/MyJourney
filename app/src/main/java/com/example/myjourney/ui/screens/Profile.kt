package com.example.myjourney.ui.screens

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myjourney.R
import com.example.myjourney.ui.Navigation.Screen
import com.example.myjourney.ui.components.AddFAB
import com.example.myjourney.ui.components.BottomNavigationBar

@Composable
fun ProfileScreen(navController: NavController,
                  isDarkMode: Boolean,
                  onDarkModeChange: (Boolean) -> Unit) {

    //state for the dark mode/light mode switching
    var notificationsEnabled by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    Scaffold(
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
            Text( //Profile Header
                text = stringResource(R.string.Profile_header),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.SemiBold
            )

            Column(// Profile Photo section
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text( text = stringResource(R.string.Profile_name), fontSize = 22.sp, fontWeight = FontWeight.Bold
                )
                Text( text = stringResource(R.string.ravindu_chamodh_email_com), color = MaterialTheme.colorScheme.outline, fontSize = 14.sp
                )

                TextButton(
                    onClick = {},
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text (
                        text = "Change Profile Picture",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            //Settings Sections
            SettingsSectionTitle(title = "Preferences")
            SettingsSwitchItem(
                title = "Dark Mode",
                subtitle = "Enable or disable dark mode",
                checked = isDarkMode,
                onCheckedChange = onDarkModeChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsSectionTitle("General")
            SettingsTextItem(
                title = "Profile Settings",
                subtitle = "Enable or disable notifications",
            )
            SettingsSwitchItem(
                title = "Notifications",
                subtitle = "Enable or disable notifications",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionTitle("About")
            SettingsTextItem(
                title = "Version",
                subtitle = "1.0.0"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logout section for logging out back to log in screen
            Button(
                onClick = {
                    // Navigate to login and clear backstack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Life Timeline v1.0.0",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            if (subtitle.isNotEmpty()) {
                Text(text = subtitle, color = MaterialTheme.colorScheme.outline, fontSize = 12.sp)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsTextItem(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        if (subtitle.isNotEmpty()) {
            Text(text = subtitle, color = MaterialTheme.colorScheme.outline, fontSize = 12.sp)
        }
    }
}


