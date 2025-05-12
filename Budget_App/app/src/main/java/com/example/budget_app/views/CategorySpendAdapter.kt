package com.example.budget_app.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_app.R
import com.example.budget_app.data.CategorySpendTotal

class CategorySpendAdapter(private val spendList: List<CategorySpendTotal>) :
    RecyclerView.Adapter<CategorySpendAdapter.SpendViewHolder>() {

    class SpendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val totalSpentTextView: TextView = itemView.findViewById(R.id.totalSpentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_total_per_category, parent, false)
        return SpendViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpendViewHolder, position: Int) {
        val item = spendList[position]
        holder.categoryNameTextView.text = item.categoryName
        holder.totalSpentTextView.text = "Total Spent: $${String.format("%.2f", item.totalSpent)}"
    }

    override fun getItemCount(): Int = spendList.size
}
