import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.budget_app.R
import com.example.budget_app.data.Expense
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

        if (!expense.imagePath.isNullOrEmpty()) {
            val context = holder.itemView.context
            try {
                val uri = Uri.parse(expense.imagePath)
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    holder.expenseImage.setImageBitmap(bitmap)
                } else {
                    holder.expenseImage.setImageResource(R.drawable.logo) // fallback
                }
            } catch (e: Exception) {
                e.printStackTrace()
                holder.expenseImage.setImageResource(R.drawable.logo) // fallback
            }
        } else {
            holder.expenseImage.setImageResource(R.drawable.logo) // fallback
        }
    }

    override fun getItemCount(): Int = expenses.size
}
