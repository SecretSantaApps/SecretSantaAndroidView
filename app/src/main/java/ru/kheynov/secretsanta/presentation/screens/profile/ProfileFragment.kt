package ru.kheynov.secretsanta.presentation.screens.profile

import android.app.AlertDialog
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
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentProfileBinding
import ru.kheynov.secretsanta.presentation.screens.profile.ProfileFragmentViewModel.Action
import ru.kheynov.secretsanta.presentation.screens.profile.ProfileFragmentViewModel.State
import ru.kheynov.secretsanta.utils.navigateToLoginScreen

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::updateUI)
            }
        }
        binding.apply {
            logoutButton.setOnClickListener { showLogoutAlertDialog(view) }
            nicknameText.setOnClickListener { viewModel.editUsername() }
            deleteProfileButton.setOnClickListener { showDeleteAccountDialog(view) }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
        viewModel.updateUsername()
    }

    private fun updateUI(state: State) {
        binding.apply {
            profileProgressBar.visibility = if (state is State.Loading) View.VISIBLE
            else View.GONE
            deleteProfileLayout.visibility = if (state is State.Loaded) View.VISIBLE
            else View.GONE
            avatarImage.visibility = if (state is State.Loaded) View.VISIBLE
            else View.GONE
            nicknameText.apply {
                if (state is State.Loaded) {
                    visibility = View.VISIBLE
                    text = state.name
                } else {
                    visibility = View.GONE
                }
            }
            logoutButton.visibility = if (state is State.Loaded) View.VISIBLE
            else View.GONE
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.NavigateToLoginScreen -> {
                AuthUI.getInstance().signOut(context!!).addOnCompleteListener {
                    navigateToLoginScreen(context!!)
                }
            }
            Action.ShowError -> Toast.makeText(context!!, "Error", Toast.LENGTH_SHORT).show()
            Action.NavigateToEditUser -> {
                findNavController().navigate(R.id.editUser)
            }
        }
    }

    private fun showLogoutAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.apply {
            setTitle(getString(R.string.logout_dialog_title))
            setMessage(getString(R.string.logout_dialog_confirmation))
            setPositiveButton(
                getString(R.string.dialog_leave_button)) { dialog, _ ->
                dialog.dismiss()
                viewModel.logout()
            }

            setNegativeButton(
                getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun showDeleteAccountDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.apply {
            setTitle(getString(R.string.delete_profile_dialog_title))
            setMessage(getString(R.string.delete_profile_dialog_confirmation))
            setPositiveButton(
                "Удалить") { dialog, _ ->
                dialog.dismiss()
                viewModel.deleteAccount()
            }

            setNegativeButton(
                getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
}

