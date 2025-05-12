import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_app.R
import com.example.budget_app.data.Expense
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses: List<Expense> = emptyList()

    fun submitList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expenseImage: ImageView = itemView.findViewById(R.id.expenseImageView)
        val expenseName: TextView = itemView.findViewById(R.id.expenseNameTextView)
        val expenseAmount: TextView = itemView.findViewById(R.id.expenseSpentTextView)
        val expenseDate: TextView = itemView.findViewById(R.id.expenseDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.expenseName.text = expense.name
        holder.expenseAmount.text = "$${expense.spent}"
        holder.expenseDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date(expense.date))
        Log.d("ImageDebug", "Image path: ${expense.imagePath}")// To log what type of file this is
        // Load image from file path
        if (!expense.imagePath.isNullOrEmpty()) {
            val file = File(expense.imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.expenseImage.setImageBitmap(bitmap)
            } else {
                holder.expenseImage.setImageResource(R.drawable.logo) // fallback
            }
        } else {
            holder.expenseImage.setImageResource(R.drawable.logo) // fallback
        }
    }

    override fun getItemCount(): Int = expenses.size
}
