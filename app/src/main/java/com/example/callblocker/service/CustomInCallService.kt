package com.example.callblocker.service

import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.callblocker.CallActivity
import com.example.callblocker.util.CallManager

@RequiresApi(Build.VERSION_CODES.M)
class CustomInCallService : InCallService() {
    
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.d("InCallService", "Call Added: ${call.details.handle}")
        
        CallManager.updateCall(call)
        
        val intent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        
        call.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                super.onStateChanged(call, state)
                CallManager.updateCall(call)
                if (state == Call.STATE_DISCONNECTED) {
                    CallManager.updateCall(null)
                }
            }
        })
    }
    
    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.d("InCallService", "Call Removed: ${call.details.handle}")
        CallManager.updateCall(null)
    }
}
