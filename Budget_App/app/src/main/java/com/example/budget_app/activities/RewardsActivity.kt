package com.example.budget_app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_app.R
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.data.Reward
import com.example.budget_app.views.RewardAdapter
import kotlinx.coroutines.launch

class RewardsActivity : AppCompatActivity() {

    private lateinit var obtainedRecyclerView: RecyclerView

    private lateinit var db: BudgetDatabase
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        userId = intent.getLongExtra("userId", -1L)
        db = BudgetDatabase.getDatabase(this)

        obtainedRecyclerView = findViewById(R.id.obtainedRecyclerView)


        obtainedRecyclerView.layoutManager = LinearLayoutManager(this)


        loadRewards()
    }

    private fun loadRewards() {
        lifecycleScope.launch {
            val obtained = db.rewardDao().getObtainedRewards(userId)



            obtainedRecyclerView.adapter = RewardAdapter(obtained)

        }
    }
}
