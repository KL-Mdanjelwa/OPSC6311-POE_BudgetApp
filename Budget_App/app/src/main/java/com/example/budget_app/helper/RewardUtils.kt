package com.example.budget_app.helper

import android.content.Context
import android.widget.Toast
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.data.Reward

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun checkAndUnlockReward(context: Context, userId: Long, title: String, description: String) {
    val rewardDao = BudgetDatabase.getDatabase(context).rewardDao()

    // Move DB access to IO thread
    val existing = withContext(Dispatchers.IO) {
        rewardDao.getRewardByTitle(userId, title)
    }

    if (existing == null || !existing.obtained) {
        withContext(Dispatchers.IO) {
            rewardDao.insertReward(
                Reward(
                    userId = userId.toInt(),
                    title = title,
                    description = description,
                    obtained = true
                )
            )
        }

        // Show toast on Main thread
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "🎉 Reward Unlocked: $title", Toast.LENGTH_LONG).show()
        }
    }
}
