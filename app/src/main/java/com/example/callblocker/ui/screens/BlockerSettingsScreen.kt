package com.example.callblocker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callblocker.util.BlockMode
import com.example.callblocker.util.PreferencesManager

@Composable
fun BlockerSettingsScreen(prefs: PreferencesManager) {
    var selectedMode by remember { mutableStateOf(prefs.blockMode) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Nível de Proteção",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SettingCard(
            title = "Desativado",
            description = "Receber todas as chamadas normalmente.",
            isSelected = selectedMode == BlockMode.NONE,
            onClick = {
                selectedMode = BlockMode.NONE
                prefs.blockMode = BlockMode.NONE
            }
        )

        SettingCard(
            title = "Bloquear Desconhecidos",
            description = "Apenas contatos da sua agenda poderão ligar.",
            isSelected = selectedMode == BlockMode.UNKNOWN_ONLY,
            onClick = {
                selectedMode = BlockMode.UNKNOWN_ONLY
                prefs.blockMode = BlockMode.UNKNOWN_ONLY
            }
        )

        SettingCard(
            title = "Bloquear Tudo",
            description = "Nenhuma chamada será recebida. Silêncio total.",
            isSelected = selectedMode == BlockMode.ALL,
            onClick = {
                selectedMode = BlockMode.ALL
                prefs.blockMode = BlockMode.ALL
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text(
                text = "Nota: Para que o bloqueio funcione corretamente, este aplicativo deve estar definido como o Discador Padrão do sistema nas configurações do Android.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun SettingCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
