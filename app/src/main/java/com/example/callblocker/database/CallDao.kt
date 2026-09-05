package com.example.callblocker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
    @Insert
    suspend fun insertCall(call: CallEntity)

    @Query("SELECT * FROM call_history ORDER BY timestamp DESC LIMIT 200")
    fun getAllCalls(): Flow<List<CallEntity>>
    
    @Query("DELETE FROM call_history")
    suspend fun clearHistory()
}
