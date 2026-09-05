package com.example.callblocker.util

import android.telecom.Call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CallManager {
    private val _currentCall = MutableStateFlow<Call?>(null)
    val currentCall: StateFlow<Call?> = _currentCall
    
    private val _callState = MutableStateFlow<Int>(Call.STATE_DISCONNECTED)
    val callState: StateFlow<Int> = _callState
    
    var inCallService: android.telecom.InCallService? = null
    
    fun updateCall(call: Call?, state: Int? = null) {
        _currentCall.value = call
        _callState.value = state ?: call?.details?.state ?: Call.STATE_DISCONNECTED
    }
    
    fun toggleMute(state: Boolean) {
        inCallService?.setMuted(state)
    }

    fun setAudioRoute(route: Int) {
        inCallService?.setAudioRoute(route)
    }
    
    fun playDtmfTone(digit: Char) {
        _currentCall.value?.playDtmfTone(digit)
    }
    
    fun stopDtmfTone() {
        _currentCall.value?.stopDtmfTone()
    }
    
    fun requestVideoCall() {
        val videoCall = _currentCall.value?.videoCall
        videoCall?.sendSessionModifyRequest(
            android.telecom.VideoProfile(android.telecom.VideoProfile.STATE_BIDIRECTIONAL)
        )
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
