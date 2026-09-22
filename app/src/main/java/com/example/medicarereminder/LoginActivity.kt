package com.example.medicareReminder

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.medicareReminder.api.ApiClient
import com.example.medicareReminder.api.AuthResponse
import com.example.medicareReminder.api.LoginRequest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Back button
        val back = findViewById<TextView>(R.id.btnLoginBack)

        back.setOnClickListener {
            finish()
        }

        // Login fields
        val email = findViewById<EditText>(R.id.etLoginEmail)

        val password = findViewById<EditText>(R.id.etLoginPassword)

        val showPassword =
            findViewById<CheckBox>(R.id.cbLoginShowPassword)

        val loginButton =
            findViewById<Button>(R.id.btnLogin)

        val registerLink =
            findViewById<TextView>(R.id.txtGoToRegister)

        // Show / hide password
        showPassword.setOnCheckedChangeListener { _, isChecked ->

            password.inputType = if (isChecked) {

                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            } else {

                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            password.setSelection(password.text.length)
        }

        // Login button
        loginButton.setOnClickListener {

            val emailText =
                email.text.toString().trim()

            val passwordText =
                password.text.toString()

            when {

                emailText.isEmpty() -> {

                    email.error = "Please enter your email"
                    email.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(emailText)
                    .matches() -> {

                    email.error =
                        "Enter a valid email address"

                    email.requestFocus()
                }

                passwordText.isEmpty() -> {

                    password.error =
                        "Please enter your password"

                    password.requestFocus()
                }

                else -> {

                    loginUser(
                        emailText,
                        passwordText
                    )
                }
            }
        }

        // Go to Register
        registerLink.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun loginUser(
        email: String,
        password: String
    ) {

        val request = LoginRequest(
            email = email,
            password = password
        )

        ApiClient.apiService.login(request)
            .enqueue(object : Callback<AuthResponse> {

                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {

                    if (response.isSuccessful) {

                        val result = response.body()

                        if (result != null) {

                            // Save logged-in user's information
                            val preferences =
                                getSharedPreferences(
                                    "MediCarePrefs",
                                    MODE_PRIVATE
                                )

                            preferences.edit()
                                .putInt(
                                    "userId",
                                    result.userId
                                )
                                .putString(
                                    "fullName",
                                    result.fullName
                                )
                                .putString(
                                    "email",
                                    result.email
                                )
                                .apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent =
                                Intent(
                                    this@LoginActivity,
                                    DashboardActivity::class.java
                                )

                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                        }

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid email or password.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<AuthResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@LoginActivity,
                        "Unable to connect to the API: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}