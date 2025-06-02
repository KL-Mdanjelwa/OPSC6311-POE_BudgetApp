package com.example.budget_app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "spent") val spent: Double,
    @ColumnInfo(name = "date") val date: Long, // Store as selected date
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "user_id") val userId: Long, // Link to User,
    @ColumnInfo(name = "image_path") val imagePath: String? = null // Optional photo
)
