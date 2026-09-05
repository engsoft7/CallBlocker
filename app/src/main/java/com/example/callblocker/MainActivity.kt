package com.example.callblocker

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowCompat
import com.example.callblocker.ui.screens.BlockerSettingsScreen
import com.example.callblocker.ui.screens.DialerScreen
import com.example.callblocker.ui.screens.HistoryScreen
import com.example.callblocker.util.PreferencesManager
import kotlinx.coroutines.launch

import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {

    private val requestRoleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "App definido como discador padrão!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissão negada. O bloqueio não funcionará.", Toast.LENGTH_LONG).show()
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkTheme = isSystemInDarkTheme()
            val colorScheme = if (darkTheme) {
                darkColorScheme(
                    primary = Color(0xFF34C759), // iOS Green
                    background = Color.Black,
                    surface = Color.Black,
                    surfaceVariant = Color(0xFF1C1C1E),
                    onBackground = Color.White
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF34C759),
                    background = Color.White,
                    surface = Color.White,
                    surfaceVariant = Color(0xFFF2F2F7),
                    onBackground = Color.Black
                )
            }

            MaterialTheme(colorScheme = colorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.safeDrawingPadding()) {
                        MainScreen(PreferencesManager(this@MainActivity))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (com.example.callblocker.util.CallManager.currentCall.value != null) {
            startActivity(Intent(this, CallActivity::class.java))
        }
    }
}

@Composable
fun MainScreen(prefs: PreferencesManager) {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    icon = { Icon(Icons.Default.Dialpad, contentDescription = "Discador") },
                    label = { Text("Discador") }
                )
                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    icon = { Icon(Icons.Default.History, contentDescription = "Histórico") },
                    label = { Text("Histórico") }
                )
                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 },
                    icon = { Icon(Icons.Default.Security, contentDescription = "Bloqueio") },
                    label = { Text("Bloqueio") }
                )
                NavigationBarItem(
                    selected = selectedIndex == 3,
                    onClick = { selectedIndex = 3 },
                    icon = { Icon(Icons.Default.Block, contentDescription = "Lista Negra") },
                    label = { Text("Lista Negra") }
                )
            }
        }
    ) { innerPadding ->
        androidx.compose.animation.Crossfade(
            targetState = selectedIndex,
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            animationSpec = androidx.compose.animation.core.tween(300)
        ) { targetIndex ->
            when (targetIndex) {
                0 -> DialerScreen()
                1 -> HistoryScreen(prefs)
                2 -> BlockerSettingsScreen(prefs)
                3 -> com.example.callblocker.ui.screens.BlacklistScreen()
            }
        }
    }
}
