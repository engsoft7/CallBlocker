package com.example.callblocker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callblocker.helper.ContactHelper

@Composable
fun DialerScreen() {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    // Resolve contact name in real-time
    var contactName by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(phoneNumber) {
        if (phoneNumber.length > 3) {
            contactName = ContactHelper.getContactName(context, phoneNumber)
        } else {
            contactName = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display Area
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (contactName != null) {
                    Text(
                        text = contactName ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(
                    text = phoneNumber,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
        }

        // Numpad
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("*", "0", "#")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    DialerButton(text = key) {
                        phoneNumber += key
                    }
                }
            }
        }

        // Action Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Empty placeholder for balance
            Box(modifier = Modifier.size(72.dp))
            
            // Call Button
            IconButton(
                onClick = {
                    if (phoneNumber.isNotEmpty()) {
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel:$phoneNumber")
                        try {
                            context.startActivity(intent)
                        } catch (e: SecurityException) {
                            // Ignore
                        }
                    }
                },
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Ligar", tint = Color.White, modifier = Modifier.size(40.dp))
            }
            
            // Delete Button
            Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                if (phoneNumber.isNotEmpty()) {
                    IconButton(onClick = { phoneNumber = phoneNumber.dropLast(1) }) {
                        Icon(Icons.Default.Clear, contentDescription = "Apagar", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

@Composable
fun DialerButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
