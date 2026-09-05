package com.example.callblocker.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklist")
data class BlacklistEntity(
    @PrimaryKey val phoneNumber: String,
    val addedAt: Long
)
