package com.example.budget_app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.budget_app.R
import com.example.budget_app.data.Category
import com.example.budget_app.data.CategoryDao
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.helper.checkAndUnlockReward
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat

class Category_ManagementActivity : AppCompatActivity() {
    private lateinit var categoryDao: CategoryDao
    private lateinit var minGoalSeekBar: SeekBar
    private lateinit var maxGoalSeekBar: SeekBar
    private lateinit var minGoalTextView: TextView
    private lateinit var maxGoalTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_man)

        // Initialize Room DB
        val db = BudgetDatabase.getDatabase(applicationContext)
        categoryDao = db.categoryDao()

        // Get the userId passed via Intent
        val userId = intent.getLongExtra("userId", -1)
        val categoryId = intent.getLongExtra("categoryId", -1)

        if (userId == -1L) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish() // Exit if userId is not valid
            return
        }

        val categoryNameEditText = findViewById<EditText>(R.id.catNameEditText)
        val categoryGoalEditText = findViewById<EditText>(R.id.catGoalEditText)
        val categoryLimitEditText = findViewById<EditText>(R.id.catLimitEditText2)
        val cancelBtn=findViewById<Button>(R.id.cancelBtn)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        val floatButton = findViewById<FloatingActionButton>(R.id.add_btn)
        minGoalTextView = findViewById(R.id.minGoalValueTextView)
        maxGoalTextView= findViewById(R.id.maxGoalValueTextView)
        minGoalSeekBar = findViewById(R.id.minGoalSeekBar)
        maxGoalSeekBar= findViewById(R.id.maxGoalSeekBar)
        lifecycleScope.launch {
            val user = db.userDao().getUserById(userId)
            cancelBtn.setOnClickListener {
                val intent = Intent(this@Category_ManagementActivity, HomeActivity::class.java)
                intent.putExtra("userId", user.userId)
                intent.putExtra("username", user.username)
                startActivity(intent)
                finish()
            }
        }
        // If categoryId is valid, prefill the fields with the existing category info
        if (categoryId != -1L) {
            lifecycleScope.launch {
                val category = categoryDao.getCategoryById(categoryId)
                runOnUiThread {
                    category?.let {
                        categoryNameEditText.setText(it.name)
                        categoryGoalEditText.setText(it.goal.toString())
                        categoryLimitEditText.setText(it.limit.toString())
                    }
                }
            }
        }

        // SeekBar for monthly minimum spending goal
        minGoalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                minGoalTextView.text = "Selected Goal: ${NumberFormat.getCurrencyInstance().format(progress)}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // SeekBar for monthly minimum spending goal
        maxGoalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                maxGoalTextView.text = "Selected Goal: ${NumberFormat.getCurrencyInstance().format(progress)}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Navigate to Item_ManagementActivity
        floatButton.setOnClickListener {
            val intent = Intent(this, Item_ManagementActivity::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("CATEGORY_ID", categoryId)
            startActivity(intent)
        }

        // Save new category or update existing category
        saveButton.setOnClickListener {
            val categoryName = categoryNameEditText.text.toString()
            val categoryGoal = categoryGoalEditText.text.toString().toDoubleOrNull()
            val categoryLimit = categoryLimitEditText.text.toString().toDoubleOrNull()
            val minGoalAmount = minGoalSeekBar.progress.toDouble()
            val maxGoalAmount = maxGoalSeekBar.progress.toDouble()




            if (categoryName.isEmpty() || categoryGoal == null || categoryLimit == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val category = Category(
                    name = categoryName,
                    goal = categoryGoal,
                    limit = categoryLimit,
                    userId = userId,
                    minGoal =minGoalAmount,
                    maxGoal = maxGoalAmount
                )


                lifecycleScope.launch {
                    checkAndUnlockReward(
                        this@Category_ManagementActivity,
                        userId,
                        "Budget Builder",
                        "You added your first category!"
                    )
                }
                lifecycleScope.launch {
                    if (categoryId != -1L) {
                        categoryDao.updateCategory(category) // Update category if categoryId is valid
                        runOnUiThread {
                            Toast.makeText(
                                this@Category_ManagementActivity,
                                "Category updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        categoryDao.insert(category) // Insert new category if categoryId is not valid
                        runOnUiThread {
                            Toast.makeText(
                                this@Category_ManagementActivity,
                                "Category saved",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                lifecycleScope.launch {
                    // Check count and unlock category reward
                    val db = BudgetDatabase.getDatabase(this@Category_ManagementActivity)
                    val count = withContext(Dispatchers.IO) {
                        db.categoryDao().getCategoriesByUser(userId).size
                    }

                    if (count > 5) {
                        checkAndUnlockReward(this@Category_ManagementActivity, userId, "Budget Architect", "You've added over 5 categories!")
                    }
                }
            }
        }
    }
}