package com.example.callblocker.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.callblocker.database.AppDatabase
import com.example.callblocker.database.CallEntity
import com.example.callblocker.helper.ContactHelper
import com.example.callblocker.util.BlockMode
import com.example.callblocker.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class CallBlockerScreeningService : CallScreeningService() {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: "Desconhecido"
        val prefs = PreferencesManager(this)
        val mode = prefs.blockMode
        
        Log.d("CallBlocker", "Receiving call from: $phoneNumber, Current Mode: $mode")
        
        val db = AppDatabase.getDatabase(applicationContext)
        val isBlacklisted = db.blacklistDao().isBlacklisted(phoneNumber)
        
        var shouldBlock = isBlacklisted
        
        if (!shouldBlock) {
            when (mode) {
                BlockMode.ALL -> {
                    shouldBlock = true
                }
                BlockMode.UNKNOWN_ONLY -> {
                    val isInContacts = ContactHelper.isNumberInContacts(this, phoneNumber)
                    if (!isInContacts) {
                        shouldBlock = true
                    }
                }
                BlockMode.NONE -> {
                    shouldBlock = false
                }
            }
        }
        
        // Save to History using Room and Coroutines
        scope.launch {
            val callEntity = CallEntity(
                phoneNumber = phoneNumber,
                timestamp = System.currentTimeMillis(),
                wasBlocked = shouldBlock
            )
            db.callDao().insertCall(callEntity)
        }
        
        val response = CallResponse.Builder()
        
        if (shouldBlock) {
            response.setDisallowCall(true)
            response.setRejectCall(true)
            response.setSkipCallLog(false)
            response.setSkipNotification(true)
            Log.d("CallBlocker", "Call from $phoneNumber blocked.")
        }
        
        respondToCall(callDetails, response.build())
    }
}
