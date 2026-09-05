package com.example.callblocker.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class BlockMode {
    NONE,
    UNKNOWN_ONLY,
    ALL
}

data class CallRecord(
    val phoneNumber: String,
    val timestamp: Long,
    val wasBlocked: Boolean
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("call_blocker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var blockMode: BlockMode
        get() {
            val modeString = prefs.getString("BLOCK_MODE", BlockMode.NONE.name)
            return try {
                BlockMode.valueOf(modeString ?: BlockMode.NONE.name)
            } catch (e: Exception) {
                BlockMode.NONE
            }
        }
        set(value) {
            prefs.edit().putString("BLOCK_MODE", value.name).apply()
        }
        
    fun addCallRecord(record: CallRecord) {
        val history = getCallHistory().toMutableList()
        history.add(0, record) // Add to the top
        // Keep only last 100 to avoid huge prefs file
        if (history.size > 100) {
            history.removeLast()
        }
        val json = gson.toJson(history)
        prefs.edit().putString("CALL_HISTORY", json).apply()
    }
    
    fun getCallHistory(): List<CallRecord> {
        val json = prefs.getString("CALL_HISTORY", null) ?: return emptyList()
        val type = object : TypeToken<List<CallRecord>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
