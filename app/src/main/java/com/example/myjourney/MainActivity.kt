package com.example.myjourney

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myjourney.ui.Navigation.AppNavigation
import com.example.myjourney.ui.screens.CreateScreen
import com.example.myjourney.ui.screens.HomeScreen
import com.example.myjourney.ui.theme.MyJourneyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemInDarkTheme) }
            var sensorLux by remember { mutableStateOf(100f) } // Default to normal light

            val context = LocalContext.current
            val moodManager = remember { com.example.myjourney.utils.AmbientMoodManager(context) }

            DisposableEffect(Unit) {
                moodManager.start { _, lux ->
                    sensorLux = lux
                }
                onDispose {
                    moodManager.stop()
                }
            }

            // GLOBAL THEME LOGIC:
            // 1. If very dark (lux < 15), FORCE Dark Mode 🌑
            // 2. If very bright (lux > 800), FORCE Light Mode ☀️
            // 3. Otherwise, use my Profile Toggle 🛋️
            val adaptiveDarkMode = when {
                sensorLux < 15f -> true
                sensorLux > 800f -> false
                else -> isDarkMode
            }

            MyJourneyTheme(darkTheme = adaptiveDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { isDarkMode = it }
                )
            }
        }
    }
}

