package com.example.budget_app.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_app.R
import com.example.budget_app.data.Reward

class RewardAdapter(private val rewardList: List<Reward>) :
    RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    inner class RewardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.rewardTitle)
        val descText: TextView = view.findViewById(R.id.rewardDescription)
        val statusText: TextView = view.findViewById(R.id.rewardStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewardList[position]
        holder.titleText.text = reward.title
        holder.descText.text = reward.description
        holder.statusText.text = if (reward.obtained) "✅ Obtained" else "❌ Locked"
    }

    override fun getItemCount(): Int = rewardList.size
}
