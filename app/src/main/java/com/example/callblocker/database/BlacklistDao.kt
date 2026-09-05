package com.example.callblocker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlacklistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNumber(blacklistEntity: BlacklistEntity)

    @Delete
    suspend fun deleteNumber(blacklistEntity: BlacklistEntity)

    @Query("SELECT * FROM blacklist ORDER BY addedAt DESC")
    fun getAllBlacklisted(): Flow<List<BlacklistEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM blacklist WHERE phoneNumber = :number)")
    fun isBlacklisted(number: String): Boolean
}
