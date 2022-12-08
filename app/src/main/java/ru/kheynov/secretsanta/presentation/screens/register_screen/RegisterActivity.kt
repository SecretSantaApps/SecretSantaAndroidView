package ru.kheynov.secretsanta.presentation.screens.register_screen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.databinding.ActivityRegisterBinding
import ru.kheynov.secretsanta.domain.entities.RegisterUser
import ru.kheynov.secretsanta.utils.navigateToLoginScreen

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            viewModel.registerUser(RegisterUser(binding.registerUsernameInput.text.toString()))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.apply {
                        registerProgressBar.visibility =
                            if (state == RegisterActivityViewModel.State.Loading) View.VISIBLE
                            else View.GONE
                        registerUsernameInput.visibility =
                            if (state is RegisterActivityViewModel.State.Idle) View.VISIBLE
                            else View.GONE

                        if (state is RegisterActivityViewModel.State.Idle) registerUsernameInput
                            .setText(viewModel.username)

                        registerButton.visibility = registerUsernameInput.visibility
                        registerTitleText.visibility = registerUsernameInput.visibility
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.actions.collect(::handleActions)
        }
    }

    private fun handleActions(action: RegisterActivityViewModel.Action) {
        when (action) {
            RegisterActivityViewModel.Action.RouteToMain -> navigateToLoginScreen(this)
            is RegisterActivityViewModel.Action.ShowError -> Toast.makeText(
                this,
                "Error: ${action.error.getText(this)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}