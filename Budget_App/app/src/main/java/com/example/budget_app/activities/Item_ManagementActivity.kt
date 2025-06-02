package com.example.budget_app.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.budget_app.R
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.data.Expense
import com.example.budget_app.helper.checkAndUnlockReward
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class Item_ManagementActivity : AppCompatActivity() {

    private lateinit var db: BudgetDatabase
    private lateinit var categorySpinner: Spinner
    private lateinit var expenseNameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var spentEditText: EditText
    private lateinit var dateButton: Button
    private lateinit var saveBtn: Button
    private lateinit var imageView: ImageView

    private var selectedDate: Long = System.currentTimeMillis()
    private var imagePath: String? = null
    private var userId: Long = 0
    private var categoryId: Long = 0

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) selectImage()
        else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imagePath = uri.toString()
            imageView.setImageURI(uri) // Show preview
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_management)

        userId = intent.getLongExtra("USER_ID", 0L)
        db = BudgetDatabase.getDatabase(applicationContext)

        categorySpinner = findViewById(R.id.categorySpinner)
        expenseNameEditText = findViewById(R.id.expenseNameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateButton = findViewById(R.id.dateBtn)
        spentEditText = findViewById(R.id.spentEditText)
        saveBtn = findViewById(R.id.saveBtn)
        val uploadBtn = findViewById<Button>(R.id.upBtn)
        imageView = findViewById(R.id.previewImage) // Add this ImageView in XML
        val cancBtn = findViewById<Button>(R.id.canceloBtn)

        lifecycleScope.launch {
            val user = db.userDao().getUserById(userId)
            cancBtn.setOnClickListener {
                val intent = Intent(this@Item_ManagementActivity, Category_ManagementActivity::class.java)
                intent.putExtra("userId", user.userId)
                startActivity(intent)
                finish()
            }
        }

        // Load categories
        lifecycleScope.launch {
            val categories = db.categoryDao().getCategoriesByUser(userId)
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(
                this@Item_ManagementActivity,
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
            if (categories.isEmpty()) {
                Toast.makeText(this@Item_ManagementActivity, "No categories found", Toast.LENGTH_LONG).show()
            }
            if (categories.isNotEmpty()) {
                categoryId = categories[0].categoryId
            }
            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                    categoryId = categories[position].categoryId
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // Date Picker
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
                dateButton.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        uploadBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    selectImage()
                } else permissionRequest.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectImage()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
                }
            }
        }

        saveBtn.setOnClickListener {
            val expenseName = expenseNameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val spent = spentEditText.text.toString().toDoubleOrNull()

            if (expenseName.isBlank() || description.isBlank() || spent == null) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("ImageDebug", "Saving imagePath: $imagePath")

            val expense = Expense(
                name = expenseName,
                spent = spent,
                date = selectedDate,
                categoryId = categoryId,
                userId = userId,
                description = description,
                imagePath = imagePath ?: "" // Use empty string if no image selected
            )

            lifecycleScope.launch {
                db.expenseDao().insert(expense)
                checkAndUnlockReward(this@Item_ManagementActivity, userId, "First Step", "You added your first expense!")

                val count = withContext(Dispatchers.IO) {
                    db.expenseDao().getExpensesByUser(userId).size
                }

                if (count > 5) {
                    checkAndUnlockReward(this@Item_ManagementActivity, userId, "Tracking Pro", "You've added over 5 expenses!")
                }

                runOnUiThread {
                    Toast.makeText(this@Item_ManagementActivity, "Expense saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectImage() {
        imagePicker.launch("image/*")
    }
}
