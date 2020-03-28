package com.rodrigues.pedroschwarz.projetofirebaseauth.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.rodrigues.pedroschwarz.projetofirebaseauth.R
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.AuthRepository
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.Failure
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.Success
import com.rodrigues.pedroschwarz.projetofirebaseauth.ui.extensions.showMessage

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerProgress: ProgressBar
    private lateinit var emailField: TextInputLayout
    private lateinit var passwordField: TextInputLayout
    private lateinit var registerBtn: Button

    private val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerProgress = findViewById(R.id.register_progress)
        emailField = findViewById(R.id.register_email)
        passwordField = findViewById(R.id.register_password)
        registerBtn = findViewById(R.id.register_create_btn)

        registerBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = emailField.editText!!.text.toString()
        val password = passwordField.editText!!.text.toString()
        if (email.isNotEmpty() && Regex(".+@.+\\..+").matches(email) && password.isNotEmpty() && password.length > 5) {
            toggleProgress()
            registerUser(email, password)
        } else {
            showMessage("Invalid data.")
        }
    }

    private fun registerUser(email: String, password: String) {
        authRepository.registerUser(email, password, onComplete = { result ->
            when (result) {
                is Success -> {
                    finish()
                }
                is Failure -> {
                    result.error?.let { error -> showMessage(error) }
                    toggleProgress()
                }
            }
        })
    }

    private fun toggleProgress() {
        when (registerProgress.visibility) {
            View.VISIBLE -> registerProgress.visibility = View.GONE
            else -> registerProgress.visibility = View.VISIBLE
        }
    }
}
