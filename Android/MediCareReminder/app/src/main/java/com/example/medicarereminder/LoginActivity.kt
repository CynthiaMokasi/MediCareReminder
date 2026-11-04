
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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.medicareReminder.api.ApiClient
import com.example.medicareReminder.api.AuthResponse
import com.example.medicareReminder.api.GoogleLoginRequest
import com.example.medicareReminder.api.LoginRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var googleButton: Button

    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        credentialManager = CredentialManager.create(this)

        val back = findViewById<TextView>(R.id.btnLoginBack)

        back.setOnClickListener {
            finish()
        }

        emailField = findViewById(R.id.etLoginEmail)
        passwordField = findViewById(R.id.etLoginPassword)

        val showPassword =
            findViewById<CheckBox>(R.id.cbLoginShowPassword)

        loginButton = findViewById(R.id.btnLogin)

        googleButton = findViewById(R.id.btnGoogleSignIn)

        val registerLink =
            findViewById<TextView>(R.id.txtGoToRegister)

        // Show or hide password.
        showPassword.setOnCheckedChangeListener { _, isChecked ->

            passwordField.inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            passwordField.setSelection(passwordField.text.length)
        }

        // Normal email and password login.
        loginButton.setOnClickListener {

            val emailText = emailField.text.toString().trim()
            val passwordText = passwordField.text.toString()

            when {

                emailText.isEmpty() -> {
                    emailField.error = "Please enter your email"
                    emailField.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(emailText)
                    .matches() -> {

                    emailField.error = "Enter a valid email address"
                    emailField.requestFocus()
                }

                passwordText.isEmpty() -> {
                    passwordField.error = "Please enter your password"
                    passwordField.requestFocus()
                }

                else -> {
                    loginUser(emailText, passwordText)
                }
            }
        }

        // Google Sign-In.
        googleButton.setOnClickListener {
            startGoogleSignIn()
        }

        // Registration.
        registerLink.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    // =====================================
    // GOOGLE SIGN-IN
    // =====================================

    private fun startGoogleSignIn() {

        googleButton.isEnabled = false
        googleButton.text = "Connecting to Google..."

        lifecycleScope.launch {

            try {

                val googleIdOption =
                    GetGoogleIdOption.Builder()
                        .setServerClientId(
                            getString(R.string.default_web_client_id)
                        )
                        .setFilterByAuthorizedAccounts(true)
                        .build()

                val request =
                    GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                try {

                    val result = credentialManager.getCredential(
                        context = this@LoginActivity,
                        request = request
                    )

                    handleGoogleCredential(result.credential)

                } catch (e: androidx.credentials.exceptions.NoCredentialException) {

                    // No previously authorised account.
                    // Allow the user to select another Google account.

                    val alternativeOption =
                        GetGoogleIdOption.Builder()
                            .setServerClientId(
                                getString(R.string.default_web_client_id)
                            )
                            .setFilterByAuthorizedAccounts(false)
                            .build()

                    val alternativeRequest =
                        GetCredentialRequest.Builder()
                            .addCredentialOption(alternativeOption)
                            .build()

                    val alternativeResult =
                        credentialManager.getCredential(
                            context = this@LoginActivity,
                            request = alternativeRequest
                        )

                    handleGoogleCredential(
                        alternativeResult.credential
                    )
                }

            } catch (e: GetCredentialException) {

                Toast.makeText(
                    this@LoginActivity,
                    "Google Sign-In cancelled or unavailable: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    this@LoginActivity,
                    "Google Sign-In error: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                googleButton.isEnabled = true
                googleButton.text = "Continue with Google"
            }
        }
    }

    private fun handleGoogleCredential(
        credential: androidx.credentials.Credential
    ) {

        if (
            credential is CustomCredential &&
            credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(
                        credential.data
                    )

                sendGoogleTokenToApi(
                    googleCredential.idToken
                )

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "Unable to read Google account details.",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {

            Toast.makeText(
                this,
                "Google account credential was not recognised.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun sendGoogleTokenToApi(idToken: String) {

        googleButton.isEnabled = false
        googleButton.text = "Verifying account..."

        val request = GoogleLoginRequest(
            idToken = idToken
        )

        ApiClient.apiService.googleLogin(request)
            .enqueue(object : Callback<AuthResponse> {

                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {

                    googleButton.isEnabled = true
                    googleButton.text = "Continue with Google"

                    if (response.isSuccessful) {

                        val result = response.body()

                        if (result != null && result.userId > 0) {

                            saveUserSession(
                                result,
                                "Google login successful!"
                            )

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "The API returned invalid account information.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Google login failed: HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<AuthResponse>,
                    t: Throwable
                ) {

                    googleButton.isEnabled = true
                    googleButton.text = "Continue with Google"

                    Toast.makeText(
                        this@LoginActivity,
                        "Unable to connect to the server.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =====================================
    // EMAIL AND PASSWORD LOGIN
    // =====================================

    private fun loginUser(
        email: String,
        password: String
    ) {

        loginButton.isEnabled = false
        loginButton.text = "Logging in..."

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

                    loginButton.isEnabled = true
                    loginButton.text = "Login"

                    if (response.isSuccessful) {

                        val result = response.body()

                        if (result != null && result.userId > 0) {

                            saveUserSession(
                                result,
                                "Login successful!"
                            )

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid account information.",
                                Toast.LENGTH_LONG
                            ).show()
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

                    loginButton.isEnabled = true
                    loginButton.text = "Login"

                    tryOfflineLogin(email)
                }
            })
    }

    // =====================================
    // SAVE USER SESSION
    // =====================================

    private fun saveUserSession(
        result: AuthResponse,
        message: String
    ) {

        val preferences = getSharedPreferences(
            "MediCarePrefs",
            MODE_PRIVATE
        )

        preferences.edit()
            .putInt("userId", result.userId)
            .putString("fullName", result.fullName)
            .putString("email", result.email)
            .putBoolean("offlineAccessEnabled", true)
            .apply()

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()

        openDashboard()
    }

    // =====================================
    // OFFLINE LOGIN
    // =====================================

    private fun tryOfflineLogin(email: String) {

        val preferences = getSharedPreferences(
            "MediCarePrefs",
            MODE_PRIVATE
        )

        val savedEmail = preferences.getString("email", "")
        val savedUserId = preferences.getInt("userId", 0)

        val offlineAccessEnabled = preferences.getBoolean(
            "offlineAccessEnabled",
            false
        )

        val emailMatches =
            email.equals(savedEmail, ignoreCase = true)

        if (
            offlineAccessEnabled &&
            emailMatches &&
            savedUserId > 0
        ) {

            Toast.makeText(
                this,
                "Internet unavailable. Opening your previously authenticated offline account.",
                Toast.LENGTH_LONG
            ).show()

            openDashboard()

        } else {

            Toast.makeText(
                this,
                "Unable to connect. Please connect to the internet and log in successfully first.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =====================================
    // OPEN DASHBOARD
    // =====================================

    private fun openDashboard() {

        val intent = Intent(
            this@LoginActivity,
            DashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}
