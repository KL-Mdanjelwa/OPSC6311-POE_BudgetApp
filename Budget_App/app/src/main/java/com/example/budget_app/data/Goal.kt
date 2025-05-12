package com.example.budget_app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val userId: Long,
    @ColumnInfo(name = "amount") val amount: Double
)
