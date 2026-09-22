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
import com.example.medicareReminder.api.RegisterRequest
import com.example.medicareReminder.api.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val back = findViewById<TextView>(R.id.btnBack)

        back.setOnClickListener {
            finish()
        }

        val fullName = findViewById<EditText>(R.id.etFullName)
        val email = findViewById<EditText>(R.id.etRegisterEmail)
        val password = findViewById<EditText>(R.id.etRegisterPassword)
        val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        val showPassword = findViewById<CheckBox>(R.id.cbShowPassword)

        val registerButton = findViewById<Button>(R.id.btnRegister)

        val loginLink = findViewById<TextView>(R.id.txtGoToLogin)

        showPassword.setOnCheckedChangeListener { _, isChecked ->

            val type = if (isChecked) {

                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            } else {

                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            password.inputType = type
            confirmPassword.inputType = type

            password.setSelection(password.text.length)
            confirmPassword.setSelection(confirmPassword.text.length)
        }

        registerButton.setOnClickListener {

            val nameText = fullName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()
            val confirmText = confirmPassword.text.toString()

            when {

                nameText.isEmpty() -> {

                    fullName.error = "Please enter your full name"
                    fullName.requestFocus()
                }

                emailText.isEmpty() -> {

                    email.error = "Please enter your email"
                    email.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(emailText)
                    .matches() -> {

                    email.error = "Please enter a valid email address"
                    email.requestFocus()
                }

                passwordText.length < 6 -> {

                    password.error =
                        "Password must contain at least 6 characters"

                    password.requestFocus()
                }

                confirmText != passwordText -> {

                    confirmPassword.error =
                        "Passwords do not match"

                    confirmPassword.requestFocus()
                }

                else -> {

                    registerButton.isEnabled = false
                    registerButton.text = "Creating Account..."

                    val request = RegisterRequest(
                        fullName = nameText,
                        email = emailText,
                        password = passwordText
                    )

                    ApiClient.apiService
                        .register(request)
                        .enqueue(object : Callback<RegisterResponse> {

                            override fun onResponse(
                                call: Call<RegisterResponse>,
                                response: Response<RegisterResponse>
                            ) {

                                registerButton.isEnabled = true
                                registerButton.text = "Create Account"

                                if (response.isSuccessful) {

                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Account created successfully!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(
                                        this@RegisterActivity,
                                        LoginActivity::class.java
                                    )

                                    startActivity(intent)

                                    finish()

                                } else {

                                    val errorBody = response.errorBody()?.string()

                                    val errorMessage = if (!errorBody.isNullOrBlank()) {
                                        errorBody
                                    } else {
                                        "HTTP ${response.code()} - ${response.message()}"
                                    }

                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Registration failed: $errorMessage",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<RegisterResponse>,
                                t: Throwable
                            ) {

                                registerButton.isEnabled = true
                                registerButton.text = "Create Account"

                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Could not connect to the API: ${t.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                }
            }
        }

        loginLink.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
        }
    }
}