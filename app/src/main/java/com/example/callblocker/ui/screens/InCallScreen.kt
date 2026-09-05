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
    var callState by remember { mutableStateOf(currentCall?.state ?: Call.STATE_DISCONNECTED) }
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
                Call.STATE_ACTIVE -> String.format("%02d:%02d", callDuration / 60, callDuration % 60)
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
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(vertical = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InCallActionButton(icon = Icons.Default.MicOff, text = "mudo")
                    InCallActionButton(icon = Icons.Default.Dialpad, text = "teclado")
                    InCallActionButton(icon = Icons.Default.VolumeUp, text = "áudio")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InCallActionButton(icon = Icons.Default.Add, text = "adicionar")
                    InCallActionButton(icon = Icons.Default.Videocam, text = "FaceTime")
                    InCallActionButton(icon = Icons.Default.Person, text = "contatos")
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
                        callState = Call.STATE_ACTIVE
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
    }
}

@Composable
fun InCallActionButton(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onBackground,
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
