package ru.kheynov.secretsanta.presentation.screens.profile

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentProfileBinding
import ru.kheynov.secretsanta.presentation.screens.profile.ProfileFragmentViewModel.Action
import ru.kheynov.secretsanta.presentation.screens.profile.ProfileFragmentViewModel.State
import ru.kheynov.secretsanta.utils.navigateToLoginScreen
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileFragmentViewModel>()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

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
            getTokenButton.setOnClickListener { getToken() }
        }
    }

    private fun getToken() {
        firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result.token.toString()
                Log.i("TOKEN", token)
                val clipboard =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clip: ClipData = ClipData.newPlainText(null, "```${token}```")
                clipboard.setPrimaryClip(clip)
                Toast.makeText(activity, "Token copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
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

            avatarImage.visibility = deleteProfileLayout.visibility
            logoutButton.visibility = deleteProfileLayout.visibility
            getTokenButton.visibility = deleteProfileLayout.visibility

            nicknameText.apply {
                if (state is State.Loaded) {
                    visibility = View.VISIBLE
                    text = state.name
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.NavigateToLoginScreen -> {
                AuthUI.getInstance().signOut(context!!).addOnCompleteListener {
                    navigateToLoginScreen(context!!)
                }
            }
            is Action.ShowError -> Toast.makeText(
                context!!,
                "Error: ${action.error.getText(context)}",
                Toast.LENGTH_SHORT
            )
                .show()
            Action.NavigateToEditUser -> {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, EditUserFragment())?.addToBackStack("")
                    ?.commit()
            }
        }
    }

    private fun showLogoutAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.apply {
            setTitle(getString(R.string.logout_dialog_title))
            setMessage(getString(R.string.logout_dialog_confirmation))
            setPositiveButton(
                getString(R.string.dialog_leave_button)
            ) { dialog, _ ->
                dialog.dismiss()
                viewModel.logout()
            }

            setNegativeButton(
                getString(R.string.cancel_button)
            ) { dialog, _ ->
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
                getString(R.string.delete)
            ) { dialog, _ ->
                dialog.dismiss()
                viewModel.deleteAccount()
            }

            setNegativeButton(
                getString(R.string.cancel_button)
            ) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
}

