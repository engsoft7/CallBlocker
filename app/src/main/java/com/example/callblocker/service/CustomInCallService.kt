package com.example.callblocker.service

import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.callblocker.CallActivity
import com.example.callblocker.util.CallManager
import com.example.callblocker.database.AppDatabase
import com.example.callblocker.database.CallEntity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
class CustomInCallService : InCallService() {
    
    override fun onBind(intent: Intent?): android.os.IBinder? {
        CallManager.inCallService = this
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        CallManager.inCallService = null
        return super.onUnbind(intent)
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.d("InCallService", "Call Added: ${call.details.handle}")
        
        if (CallManager.currentCall.value != null && CallManager.currentCall.value != call) {
            Log.d("InCallService", "Multiple calls not supported. Rejecting new call.")
            call.reject(false, null)
            return
        }
        
        CallManager.updateCall(call)
        val phoneNumber = call.details.handle?.schemeSpecificPart ?: "Desconhecido"
        showOngoingCallNotification(phoneNumber)
        
        // Registrar chamadas SAINTES no histórico
        if (call.details.state == Call.STATE_CONNECTING || call.details.state == Call.STATE_DIALING) {
            val phoneNumber = call.details.handle?.schemeSpecificPart ?: "Desconhecido"
            val db = AppDatabase.getDatabase(applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                db.callDao().insertCall(
                    CallEntity(
                        phoneNumber = phoneNumber,
                        timestamp = System.currentTimeMillis(),
                        wasBlocked = false
                    )
                )
            }
        }
        
        val intent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        
        call.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                super.onStateChanged(call, state)
                CallManager.updateCall(call, state)
                if (state == Call.STATE_DISCONNECTED) {
                    CallManager.updateCall(null, state)
                }
            }
        })
    }
    
    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.d("InCallService", "Call Removed: ${call.details.handle}")
        CallManager.updateCall(null)
        hideOngoingCallNotification()
    }
    
    private fun showOngoingCallNotification(phoneNumber: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ongoing_call_channel"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chamada em Andamento",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        val intent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Chamada em andamento")
            .setContentText(phoneNumber)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
            
        notificationManager.notify(1001, notification)
    }

    private fun hideOngoingCallNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001)
    }
}
