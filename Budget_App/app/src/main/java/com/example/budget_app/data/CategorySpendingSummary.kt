package com.example.budget_app.data

import androidx.room.ColumnInfo

data class CategorySpendingSummary(
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "totalSpent") val totalSpent: Double
)
