package com.example.budget_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses WHERE user_id = :userId")
    suspend fun getExpensesByUser(userId: Long): List<Expense>

    @Query("SELECT * FROM expenses WHERE user_id = :userId AND category_id = :categoryId")
    suspend fun getExpensesByCategory(userId: Long, categoryId: Long): List<Expense>



    @Query("SELECT * FROM expenses WHERE user_id = :userId AND date BETWEEN :start AND :end")
    suspend fun getExpensesByDateRange(userId: Long, start: Long, end: Long): List<Expense>


    @Query("""
        SELECT c.name AS categoryName, 
               SUM(e.spent) AS totalSpent
        FROM expenses e
        INNER JOIN categories c ON e.category_id = c.categoryId
        WHERE e.user_id = :userId
        GROUP BY e.category_id
    """)
    suspend fun getTotalSpentPerCategory(userId: Long): List<CategorySpendTotal>

    @Query("""
    SELECT c.name AS categoryName, SUM(e.spent) AS totalSpent
    FROM expenses e
    INNER JOIN categories c ON e.category_id = c.categoryId
    WHERE e.user_id = :userId AND e.date BETWEEN :startDate AND :endDate
    GROUP BY e.category_id
""")
    suspend fun getTotalSpentPerCategoryBetweenDates(
        userId: Long,
        startDate: Long,
        endDate: Long
    ): List<CategorySpendTotal>
}

