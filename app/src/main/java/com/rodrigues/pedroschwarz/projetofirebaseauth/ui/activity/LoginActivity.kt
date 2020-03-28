package com.rodrigues.pedroschwarz.projetofirebaseauth.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.rodrigues.pedroschwarz.projetofirebaseauth.R
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.AuthRepository
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.Failure
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.Success
import com.rodrigues.pedroschwarz.projetofirebaseauth.ui.extensions.showMessage

private const val RC_SIGN_IN = 1

class LoginActivity : AppCompatActivity() {

    private val authRepository by lazy {
        AuthRepository()
    }

    private lateinit var googleClient: GoogleSignInClient

    private lateinit var loginProgress: ProgressBar
    private lateinit var emailField: TextInputLayout
    private lateinit var passwordField: TextInputLayout
    private lateinit var loginGoogleBtn: SignInButton
    private lateinit var loginBtn: Button
    private lateinit var loginRegisterBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)

        loginProgress = findViewById(R.id.login_progress)
        emailField = findViewById(R.id.login_email)
        passwordField = findViewById(R.id.login_password)
        loginGoogleBtn = findViewById(R.id.login_google_btn)
        loginGoogleBtn.setSize(SignInButton.SIZE_WIDE)
        loginBtn = findViewById(R.id.login_enter_btn)
        loginRegisterBtn = findViewById(R.id.login_register_btn)

        loginBtn.setOnClickListener {
            validateData()
        }

        loginRegisterBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        loginGoogleBtn.setOnClickListener {
            val intent = googleClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onResume() {
        super.onResume()
        checkUser()
    }

    private fun checkUser() {
        when (authRepository.checkUser(this)) {
            is Success -> {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun validateData() {
        val email = emailField.editText!!.text.toString()
        val password = passwordField.editText!!.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            toggleProgress()
            loginUser(email, password)
        } else {
            showMessage("Invalid data.")
        }
    }

    private fun loginUser(email: String, password: String) {
        authRepository.loginUser(email, password, onComplete = { result ->
            when (result) {
                is Success -> {
                    checkUser()
                }
                is Failure -> {
                    toggleProgress()
                    result.error?.let { error -> showMessage(error) }
                }
            }
        })
    }

    private fun toggleProgress() {
        when (loginProgress.visibility) {
            View.VISIBLE -> loginProgress.visibility = View.GONE
            else -> loginProgress.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            completedTask.getResult(ApiException::class.java)
            checkUser()
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showMessage("Something went wrong.")
        }
    }
}
