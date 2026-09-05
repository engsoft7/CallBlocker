package com.example.callblocker.util

import android.telecom.Call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CallManager {
    private val _currentCall = MutableStateFlow<Call?>(null)
    val currentCall: StateFlow<Call?> = _currentCall
    
    fun updateCall(call: Call?) {
        _currentCall.value = call
    }
    
    fun answerCall() {
        _currentCall.value?.answer(0)
    }
    
    fun rejectCall() {
        _currentCall.value?.reject(false, null)
    }
    
    fun disconnectCall() {
        _currentCall.value?.disconnect()
    }
}
