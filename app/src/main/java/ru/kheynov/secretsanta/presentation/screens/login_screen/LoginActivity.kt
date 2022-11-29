package ru.kheynov.secretsanta.presentation.screens.login_screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.data.KeyValueStorage
import ru.kheynov.secretsanta.databinding.ActivityLoginBinding
import ru.kheynov.secretsanta.domain.use_cases.UseCases
import ru.kheynov.secretsanta.presentation.MainActivity
import ru.kheynov.secretsanta.presentation.screens.register_screen.RegisterActivity
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject

private const val TAG = "LoginActivity"

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var keyValueStorage: KeyValueStorage

    @Inject
    lateinit var useCases: UseCases

    private lateinit var binding: ActivityLoginBinding

    private val state = MutableStateFlow<State>(State.Idle)

    private sealed interface State {
        object Idle : State
        object Loading : State
    }

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            this.onSignInResult(res)
        }

    private val providers = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                state.collect { state ->
                    binding.apply {
                        loginProgressBar.visibility = if (state == State.Loading) View.VISIBLE
                        else View.GONE
                        signInButton.visibility = if (state == State.Idle) View.VISIBLE
                        else View.GONE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (firebaseAuth.currentUser != null) {
            lifecycleScope.launch {
                state.value = State.Loading
                when (val res = useCases.checkUserRegistered()) {
                    is Resource.Failure -> {
                        keyValueStorage.isAuthorized = false
                        state.value = State.Idle
                        Log.e(TAG, "Something went wrong", res.exception)
                        Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        keyValueStorage.isAuthorized = res.result
                        state.value = State.Idle
                        if (res.result)
                            navigateToMainActivity()
                        else
                            navigateToRegisterActivity()

                    }
                }
            }
        }
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Toast.makeText(applicationContext, "Authenticated!", Toast.LENGTH_SHORT).show()
        } else {
            //TODO: show beautiful message to user, that he cancelled request
            Toast.makeText(this, "Why u cancelling", Toast.LENGTH_SHORT).show()
        }
    }
}