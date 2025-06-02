package com.example.budget_app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.budget_app.R
import com.example.budget_app.data.User
import com.example.budget_app.data.BudgetDatabase
import com.example.budget_app.helper.checkAndUnlockReward
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val usernameEditText = findViewById<EditText>(R.id.userNameText)
        val passwordEditText = findViewById<EditText>(R.id.passText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.passConfirmText)
        val loginRedirectText = findViewById<TextView>(R.id.loginRedirectText)

        registerBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = BudgetDatabase.getDatabase(this)
            val user = User(username = username, password = password)

            lifecycleScope.launch {
                val existingUser = db.userDao().getUserByUsername(username)
                if (existingUser != null) {
                    Toast.makeText(this@MainActivity, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    db.userDao().insertUser(user)
                    val newUser = db.userDao().getUserByUsername(username)
                    newUser?.let {
                        val userId: Long = it.userId // Keep as Long
                        checkAndUnlockReward(
                            context = this@MainActivity,
                            userId = userId,
                            title = "Welcome Aboard",
                            description  = "You created your first account!"
                        )
                    }

                    Toast.makeText(this@MainActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                }
            }
        }

        loginRedirectText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
