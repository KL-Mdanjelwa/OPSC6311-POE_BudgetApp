package com.example.budget_app.activities

import ExpenseAdapter
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_app.R
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.data.ExpenseDao
import com.example.budget_app.views.CategoryAdapter
import com.example.budget_app.views.CategorySpendAdapter
import com.example.budget_app.views.CategorySpentAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var db: BudgetDatabase
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var totalsRecyclerView: RecyclerView

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var categorySpendAdapter: CategorySpendAdapter

    private lateinit var startDateButton: Button
    private lateinit var endDateButton: Button
    private lateinit var filterButton: Button

    private lateinit var scrollView: NestedScrollView
    private lateinit var categorySection: View
    private lateinit var expenseSection: View
    private lateinit var totalsSection: View

    private var startDate: Long = 0L
    private var endDate: Long = Long.MAX_VALUE
    private var userId: Long = -1L

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Sections
        scrollView = findViewById(R.id.mainScrollView)
        categorySection = findViewById(R.id.categorySection)
        expenseSection = findViewById(R.id.expenseSection)
        totalsSection = findViewById(R.id.totalsSection)

        // Navigation item actions
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_category -> scrollToView(categorySection)
                R.id.nav_item_expense -> scrollToView(expenseSection)
                R.id.nav_item_category_spent -> scrollToView(totalsSection)
                R.id.nav_item_category -> {
                    val intent = Intent(this, Category_ManagementActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Database and intent data
        db = BudgetDatabase.getDatabase(this)
        userId = intent.getLongExtra("userId", -1L)
        val username = intent.getStringExtra("username") ?: return


        loadCategories(userId)

        // UI Views
        val goalInput = findViewById<EditText>(R.id.goalInput)
        val saveGoalButton = findViewById<Button>(R.id.saveGoalButton)
        val categoryBtn = findViewById<FloatingActionButton>(R.id.floatBtn2)
        startDateButton = findViewById(R.id.startDateButton)
        endDateButton = findViewById(R.id.endDateButton)
        filterButton = findViewById(R.id.filterButton)

        // RecyclerViews
        categoryRecyclerView = findViewById(R.id.categoriesRecyclerView)
        expenseRecyclerView = findViewById(R.id.totalsRecyclerView)
        totalsRecyclerView = findViewById(R.id.categorySpendRecyclerView)




        categoryAdapter = CategoryAdapter()
        expenseAdapter = ExpenseAdapter()
        categorySpendAdapter = CategorySpendAdapter(emptyList())


        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        expenseRecyclerView.layoutManager = LinearLayoutManager(this)
        totalsRecyclerView.layoutManager = LinearLayoutManager(this)

        categoryRecyclerView.adapter = categoryAdapter
        expenseRecyclerView.adapter = expenseAdapter
        totalsRecyclerView.adapter = categorySpendAdapter

        // Date Pickers
        startDateButton.setOnClickListener { showDatePicker(true) }
        endDateButton.setOnClickListener { showDatePicker(false) }

        // Filter button
        filterButton.setOnClickListener {
            loadCategories(userId)
            loadExpensesForPeriod(userId, startDate, endDate)
            loadTotalsForUserBetweenDates(userId, startDate, endDate)
        }

        // Save goal
        saveGoalButton.setOnClickListener {
            val amount = goalInput.text.toString().toDoubleOrNull()
            if (amount != null) {
                lifecycleScope.launch {
                    db.userDao().updateBudgetGoal(userId, amount)
                    Toast.makeText(this@HomeActivity, "Budget goal updated", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@HomeActivity, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        // Floating action button
        categoryBtn.setOnClickListener {
            val intent = Intent(this, Category_ManagementActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }

        // Load goal from DB
        lifecycleScope.launch {
            db.userDao().getUserByUsername(username)?.let {
                goalInput.setText(it.budgetGoal?.toString())
            }
        }

        // Initial data loads

    }

    private fun scrollToView(view: View) {
        scrollView.post {
            scrollView.smoothScrollTo(0, view.top)
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day, 0, 0, 0)
            val selectedDate = cal.timeInMillis
            if (isStartDate) {
                startDate = selectedDate
                startDateButton.text = "Start: $day/${month + 1}/$year"
            } else {
                endDate = selectedDate + 86399999
                endDateButton.text = "End: $day/${month + 1}/$year"
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun loadCategories(userId: Long) {
        lifecycleScope.launch {
            val categories = db.categoryDao().getCategoriesByUser(userId)
            categoryAdapter.submitList(categories)
        }
    }

    private fun loadExpensesForPeriod(userId: Long, start: Long, end: Long) {
        lifecycleScope.launch {
            val expenses = db.expenseDao().getExpensesByDateRange(userId, start, end)
            expenseAdapter.submitList(expenses)
        }
    }



    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }

    private fun loadTotalsForUserBetweenDates(userId: Long, startDate: Long, endDate: Long) {
        val expenseDao = BudgetDatabase.getDatabase(this).expenseDao()
        val recyclerView = findViewById<RecyclerView>(R.id.categorySpendRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val spendList = expenseDao.getTotalSpentPerCategoryBetweenDates(userId, startDate, endDate)
            runOnUiThread {
                recyclerView.adapter = CategorySpendAdapter(spendList)
            }
        }
    }
}