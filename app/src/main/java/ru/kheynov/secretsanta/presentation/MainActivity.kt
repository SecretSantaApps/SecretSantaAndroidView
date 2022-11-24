package ru.kheynov.secretsanta.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import ru.kheynov.secretsanta.R

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var logoutButton: MaterialButton
    private lateinit var signinButton: MaterialButton
    private lateinit var getTokenButton: MaterialButton

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        logoutButton = findViewById(R.id.logoutButton)
        signinButton = findViewById(R.id.signInButton)
        getTokenButton = findViewById(R.id.getTokenButton)

        logoutButton.setOnClickListener {
            AuthUI.getInstance().signOut(this).addOnCompleteListener {
                Log.i("Auth", "Logged out")
            }
        }

        val providers = arrayListOf(
//            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        signinButton.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }

        getTokenButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.getIdToken(false)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken: String? = task.result?.token

                    Log.i("TOKEN", idToken.toString())
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    val clip: ClipData = ClipData.newPlainText(null, "```${idToken.toString()}```")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show()

                    // Send token to your backend via HTTPS
                    // ...
                } else {
                    // Handle error -> task.getException();
                }
            }
        }

    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Toast.makeText(applicationContext, "Authenticated!", Toast.LENGTH_SHORT).show()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
}