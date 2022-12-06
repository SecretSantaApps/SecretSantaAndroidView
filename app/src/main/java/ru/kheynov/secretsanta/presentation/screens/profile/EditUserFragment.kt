package ru.kheynov.secretsanta.presentation.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.databinding.FragmentEditUserBinding
import ru.kheynov.secretsanta.presentation.screens.profile.EditUserViewModel.Action
import ru.kheynov.secretsanta.presentation.screens.profile.EditUserViewModel.State

@AndroidEntryPoint
class EditUserFragment : Fragment() {

    private val viewModel by viewModels<EditUserViewModel>()

    private lateinit var binding: FragmentEditUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::updateUI)
            }
        }
        binding.editUserSaveButton.setOnClickListener {
            viewModel.saveUsername(binding.editUsernameInput.text.toString())
        }
    }

    private fun updateUI(state: State) {
        binding.apply {
            editUserProgressBar.visibility =
                if (state == State.Loading) View.VISIBLE
                else View.GONE
            userEditTitle.visibility =
                if (state is State.Loaded) View.VISIBLE
                else View.GONE
            editUserSaveButton.visibility = userEditTitle.visibility
            editUsernameInput.apply {
                if (state is State.Loaded) {
                    visibility =
                        View.VISIBLE
                    setText(state.username)
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.NavigateBack -> activity?.supportFragmentManager?.popBackStack()
            Action.ShowError -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

}