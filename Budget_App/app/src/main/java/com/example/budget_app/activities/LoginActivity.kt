package com.example.budget_app.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.budget_app.R
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.data.UserDao
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = BudgetDatabase.getDatabase(this)
        userDao = db.userDao()

        val loginButton = findViewById<Button>(R.id.loginBtn)
        val registerRedirectText = findViewById<TextView>(R.id.registerRedirectText)
        val usernameEditText = findViewById<EditText>(R.id.userNameText2)
        val passwordEditText = findViewById<EditText>(R.id.passText2)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().getUserByUsername(username)

                if (user != null && user.password == password) {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("userId", user.userId) // Important
                    intent.putExtra("username", user.username) // Also important
                    val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    with(sharedPrefs.edit()) {
                        putLong("userId", user.userId)
                        apply()
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            registerRedirectText.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }
}
