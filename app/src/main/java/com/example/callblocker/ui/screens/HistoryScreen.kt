package com.example.callblocker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callblocker.database.AppDatabase
import com.example.callblocker.database.CallEntity
import com.example.callblocker.helper.ContactHelper
import com.example.callblocker.util.PreferencesManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(prefs: PreferencesManager) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val history by db.callDao().getAllCalls().collectAsState(initial = emptyList())

    var hasContactsPermission by remember { 
        mutableStateOf(androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED) 
    }
    val contactsPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
        if (!isGranted) {
            android.widget.Toast.makeText(context, "Permissão negada. Nomes não serão exibidos.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Histórico",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (history.isNotEmpty()) {
                val coroutineScope = rememberCoroutineScope()
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            db.callDao().clearHistory()
                        }
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Limpar Histórico", tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (!hasContactsPermission && history.isNotEmpty()) {
            TextButton(
                onClick = { contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS) },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Permitir acesso aos contatos para ver nomes", color = MaterialTheme.colorScheme.primary)
            }
        }

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Nenhuma chamada registrada.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { record ->
                    HistoryItem(record = record, hasContactsPermission = hasContactsPermission)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(record: CallEntity, hasContactsPermission: Boolean) {
    val context = LocalContext.current
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = formatter.format(Date(record.timestamp))
    
    // Resolve contact name
    var displayName by remember { mutableStateOf(record.phoneNumber) }
    LaunchedEffect(record.phoneNumber, hasContactsPermission) {
        if (hasContactsPermission) {
            val contactName = ContactHelper.getContactName(context, record.phoneNumber)
            if (contactName != null) {
                displayName = contactName
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (record.wasBlocked) Color(0xFFE53935).copy(alpha = 0.2f) else Color(0xFF4CAF50).copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (record.wasBlocked) Icons.Default.Clear else Icons.Default.Phone,
                    contentDescription = null,
                    tint = if (record.wasBlocked) Color(0xFFE53935) else Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (displayName != record.phoneNumber) {
                    Text(
                        text = record.phoneNumber,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                if (record.wasBlocked) {
                    Text(
                        text = "Bloqueada pelo App",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFE53935),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
