package com.example.callblocker.ui.screens

import android.telecom.Call
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callblocker.helper.ContactHelper
import com.example.callblocker.util.CallManager
import kotlinx.coroutines.delay

@Composable
fun InCallScreen(onEndCall: () -> Unit) {
    val currentCall by CallManager.currentCall.collectAsState()
    val callState by CallManager.callState.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(currentCall) {
        if (currentCall == null) {
            onEndCall()
        }
    }

    // A simple timer for active calls
    var callDuration by remember { mutableStateOf(0) }
    LaunchedEffect(callState) {
        if (callState == Call.STATE_ACTIVE) {
            while (true) {
                delay(1000)
                callDuration++
            }
        }
    }
    
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }
    var showKeypad by remember { mutableStateOf(false) }
    var showAudioRouteDialog by remember { mutableStateOf(false) }
    
    val phoneNumber = currentCall?.details?.handle?.schemeSpecificPart ?: "Desconhecido"
    var contactName by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(phoneNumber) {
        contactName = ContactHelper.getContactName(context, phoneNumber)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 64.dp, bottom = 48.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = contactName ?: phoneNumber,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 36.sp,
                fontWeight = FontWeight.Normal
            )
            if (contactName != null) {
                Text(
                    text = phoneNumber,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            val stateText = when (callState) {
                Call.STATE_RINGING -> "Tocando..."
                Call.STATE_ACTIVE -> {
                    if (callDuration >= 3600) {
                        String.format("%02d:%02d:%02d", callDuration / 3600, (callDuration % 3600) / 60, callDuration % 60)
                    } else {
                        String.format("%02d:%02d", callDuration / 60, callDuration % 60)
                    }
                }
                Call.STATE_DISCONNECTED -> "Desconectado"
                else -> "Conectando..."
            }
            Text(
                text = stateText,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 20.sp
            )
        }

        if (callState == Call.STATE_ACTIVE || callState == Call.STATE_CONNECTING || callState == Call.STATE_DIALING) {
            // Middle section (iOS grid)
            if (showKeypad) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("*", "0", "#")
                    )
                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                        .clickable { 
                                            CallManager.playDtmfTone(key.first())
                                            // Optional: Stop tone shortly after
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showKeypad = false }) {
                        Text("Ocultar Teclado", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.padding(vertical = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InCallActionButton(icon = Icons.Default.MicOff, text = "mudo", isActive = isMuted) {
                            isMuted = !isMuted
                            CallManager.toggleMute(isMuted)
                        }
                        InCallActionButton(icon = Icons.Default.Dialpad, text = "teclado") {
                            showKeypad = true
                        }
                        InCallActionButton(icon = Icons.Default.VolumeUp, text = "áudio", isActive = isSpeakerOn) {
                            showAudioRouteDialog = true
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InCallActionButton(icon = Icons.Default.Add, text = "adicionar") {
                            android.widget.Toast.makeText(context, "Conferência não suportada", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        InCallActionButton(icon = Icons.Default.Videocam, text = "vídeo") {
                            try {
                                val number = phoneNumber.replace(Regex("[^0-9]"), "")
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://api.whatsapp.com/send?phone=$number"))
                                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "WhatsApp indisponível", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                        InCallActionButton(icon = Icons.Default.Person, text = "contatos") {
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.provider.ContactsContract.Contacts.CONTENT_URI)
                                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            } catch (e: Exception) {}
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Bottom buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (callState == Call.STATE_RINGING) {
                // Reject Button
                IconButton(
                    onClick = { 
                        CallManager.rejectCall()
                        onEndCall()
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Recusar", tint = Color.White, modifier = Modifier.size(36.dp))
                }
                
                // Answer Button
                IconButton(
                    onClick = { 
                        CallManager.answerCall()
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF34C759), CircleShape)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Atender", tint = Color.White, modifier = Modifier.size(36.dp))
                }
            } else {
                // End Call Button
                IconButton(
                    onClick = { 
                        CallManager.disconnectCall()
                        onEndCall()
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Encerrar", tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
        }
        
        if (showAudioRouteDialog) {
            AlertDialog(
                onDismissRequest = { showAudioRouteDialog = false },
                title = { Text("Saída de Áudio") },
                text = {
                    Column {
                        TextButton(
                            onClick = { 
                                CallManager.setAudioRoute(android.telecom.CallAudioState.ROUTE_EARPIECE)
                                isSpeakerOn = false
                                showAudioRouteDialog = false 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Telefone (Ouvido)", fontSize = 18.sp) }
                        
                        TextButton(
                            onClick = { 
                                CallManager.setAudioRoute(android.telecom.CallAudioState.ROUTE_SPEAKER)
                                isSpeakerOn = true
                                showAudioRouteDialog = false 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Viva-Voz", fontSize = 18.sp) }
                        
                        TextButton(
                            onClick = { 
                                CallManager.setAudioRoute(android.telecom.CallAudioState.ROUTE_BLUETOOTH)
                                isSpeakerOn = false
                                showAudioRouteDialog = false 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Bluetooth", fontSize = 18.sp) }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAudioRouteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun InCallActionButton(icon: ImageVector, text: String, isActive: Boolean = false, onClick: () -> Unit = {}) {
    val bgColor = if (isActive) Color.White else MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (isActive) Color.Black else MaterialTheme.colorScheme.onBackground
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )
    }
}
