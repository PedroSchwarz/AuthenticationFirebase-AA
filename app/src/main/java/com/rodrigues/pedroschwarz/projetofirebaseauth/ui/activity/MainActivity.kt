package com.rodrigues.pedroschwarz.projetofirebaseauth.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.rodrigues.pedroschwarz.projetofirebaseauth.R
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.AuthRepository
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.Failure
import com.rodrigues.pedroschwarz.projetofirebaseauth.repository.Success

class MainActivity : AppCompatActivity() {

    private val authRepository by lazy {
        AuthRepository()
    }

    private lateinit var googleClient: GoogleSignInClient
    private var googleAccount: GoogleSignInAccount? = null

    private lateinit var userImage: ImageView
    private lateinit var userEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

        googleClient = GoogleSignIn.getClient(this, gso)
        googleAccount = GoogleSignIn.getLastSignedInAccount(this)

        userImage = findViewById(R.id.user_image)
        userEmail = findViewById(R.id.user_email)

        if (googleAccount != null) {
            setGoogleAccountData()
        } else {
            getFirebaseUser()
        }
    }

    override fun onResume() {
        super.onResume()
        checkUser()
    }

    private fun setGoogleAccountData() {
        userImage.visibility = View.VISIBLE
        googleAccount?.let {
            Glide.with(this@MainActivity).load(it.photoUrl).into(userImage)
            userEmail.text = it.email
        }
    }

    private fun getFirebaseUser() {
        when (val resource = authRepository.getUser()) {
            is Success -> {
                resource.data?.let { data ->
                    userEmail.text = data.email
                }
            }
        }
    }

    private fun checkUser() {
        when (authRepository.checkUser(this)) {
            is Failure -> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_sign_out_action -> {
                if (googleAccount != null) {
                    authRepository.signOutGoogleAccount(googleClient, onComplete = { result ->
                        checkUser()
                    })
                } else {
                    authRepository.signOutUser()
                    checkUser()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
