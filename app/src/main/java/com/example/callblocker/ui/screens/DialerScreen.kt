package com.example.callblocker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialerScreen() {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    var hasContactsPermission by remember { 
        mutableStateOf(androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED) 
    }
    val contactsPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
        if (!isGranted) {
            android.widget.Toast.makeText(context, "Permissão de contatos negada. Nomes não serão exibidos.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val callPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            try { context.startActivity(intent) } catch (e: SecurityException) {}
        } else if (!isGranted) {
            android.widget.Toast.makeText(context, "Precisamos da permissão de telefone para realizar a chamada. Nenhum dado é coletado.", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    // Resolve contact name in real-time
    var contactName by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(phoneNumber, hasContactsPermission) {
        if (hasContactsPermission && phoneNumber.length > 3) {
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
                if (!hasContactsPermission) {
                    TextButton(onClick = { contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS) }) {
                        Text("Permitir acesso aos contatos para ver nomes", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
                if (contactName != null) {
                    Text(
                        text = contactName ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                if (phoneNumber.startsWith("0303")) {
                    Text(
                        text = "⚠️ Possível Telemarketing",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                val formattedNumber = android.telephony.PhoneNumberUtils.formatNumber(phoneNumber, "BR") ?: phoneNumber
                Text(
                    text = formattedNumber,
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
                    DialerButton(
                        text = key,
                        onClick = { phoneNumber += key },
                        onLongClick = if (key == "1") {
                            {
                                if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    val intent = Intent(Intent.ACTION_CALL)
                                    intent.data = Uri.parse("voicemail:")
                                    try { context.startActivity(intent) } catch (e: Exception) {}
                                } else {
                                    callPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                                }
                            }
                        } else null
                    )
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
            val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    if (phoneNumber.isNotEmpty()) {
                        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            val intent = Intent(Intent.ACTION_CALL)
                            intent.data = Uri.parse("tel:$phoneNumber")
                            try {
                                context.startActivity(intent)
                            } catch (e: SecurityException) {
                                // Ignore
                            }
                        } else {
                            callPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
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
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .combinedClickable(
                                onClick = { 
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                    phoneNumber = phoneNumber.dropLast(1) 
                                },
                                onLongClick = { 
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    phoneNumber = "" 
                                }
                            )
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Apagar", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialerButton(text: String, onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .combinedClickable(
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    onClick()
                },
                onLongClick = onLongClick?.let {
                    {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        it()
                    }
                }
            ),
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
