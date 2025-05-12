package com.example.budget_app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "goal") val goal: Double,
    @ColumnInfo(name = "limit") val limit: Double,
    @ColumnInfo(name = "user_id") val userId: Long,// Link to User
    @ColumnInfo(name = "min_goal") val minGoal: Double,
    @ColumnInfo(name = "max_goal") val maxGoal: Double

)
