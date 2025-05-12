package com.example.budget_app.views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_app.R
import com.example.budget_app.data.CategorySpendTotal


class CategorySpentAdapter : RecyclerView.Adapter<CategorySpentAdapter.CategorySpentViewHolder>() {

    private var categorySpentList = listOf<CategorySpendTotal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySpentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_spent, parent, false)
        return CategorySpentViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CategorySpentViewHolder, position: Int) {
        val categorySpent = categorySpentList[position]

        // Display categoryId and total spent
        holder.categoryTextView.text =
            "Category Name: ${categorySpent.categoryName}" // Displaying category ID
        holder.totalSpentTextView.text = "$${String.format("%.2f", categorySpent.totalSpent)}"  // Format total spent to 2 decimal places
    }

    override fun getItemCount(): Int = categorySpentList.size

    fun submitList(categorySpent: List<CategorySpendTotal>) {
        categorySpentList = categorySpent
        notifyDataSetChanged()
    }

    inner class CategorySpentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTextView: TextView = view.findViewById(R.id.categoryNameTextView)
        val totalSpentTextView: TextView = view.findViewById(R.id.totalSpentTextView)
    }
}
