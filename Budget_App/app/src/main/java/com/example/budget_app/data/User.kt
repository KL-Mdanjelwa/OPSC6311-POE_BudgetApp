package com.example.budget_app.data

import androidx.room.ColumnInfo // Used for naming the columns in the Entity
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "budget_goal") val budgetGoal: Double = 0.0 // <- this line is critical
)


