package com.example.budget_app.helper

import android.content.Context
import android.widget.Toast
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.data.Reward
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object RewardManager {

    fun unlockRewardIfNeeded(context: Context, rewardTitle: String, userId: Long) {
        val db = BudgetDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            val existing = db.rewardDao().getAllRewardsForUser(userId)
            val alreadyUnlocked = existing.any { it.title == rewardTitle }

            if (!alreadyUnlocked) {
                val reward = when (rewardTitle) {
                    "Welcome!" -> Reward(title = "Welcome!", description = "Thanks for signing up!", obtained = true, userId = userId.toInt())
                    "First Category Created" -> Reward(title = "First Category Created", description = "You've created your first category!", obtained = true, userId = userId.toInt())
                    "First Expense Logged" -> Reward(title = "First Expense Logged", description = "You logged your first expense!", obtained = true, userId = userId.toInt())
                    "Goal Setter" -> Reward(title = "Goal Setter", description = "You set a budget goal!", obtained = true, userId = userId.toInt())
                    else -> null
                }

                reward?.let {
                    db.rewardDao().insertReward(it)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "🎉 Reward Unlocked: ${it.title}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
