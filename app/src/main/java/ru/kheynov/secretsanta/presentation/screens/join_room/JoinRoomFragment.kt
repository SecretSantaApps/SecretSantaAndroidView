package ru.kheynov.secretsanta.presentation.screens.join_room

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.databinding.FragmentJoinRoomBinding
import ru.kheynov.secretsanta.presentation.MainActivityViewModel

@AndroidEntryPoint
class JoinRoomFragment : Fragment() {

    private lateinit var binding: FragmentJoinRoomBinding

    private val viewModel by viewModels<JoinRoomViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            arguments?.getString("roomId")?.let {
                roomIdInput.setText(it)
            }
            arguments?.getString("pass")?.let {
                roomPasswordInput.setText(it)
                if (!roomPasswordInput.text.isNullOrBlank() && !roomIdInput.text.isNullOrBlank()) {
                    viewModel.joinRoom(
                        roomId = roomIdInput.text.toString(),
                        password = roomPasswordInput.text.toString()
                    )
                }
            }
            joinRoomButton.setOnClickListener {
                viewModel.joinRoom(
                    roomId = roomIdInput.text.toString(),
                    password = roomPasswordInput.text.toString()
                )
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::updateUI)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect(::handleAction)
            }
        }
    }

    private fun handleAction(action: JoinRoomViewModel.Action) {
        when (action) {
            JoinRoomViewModel.Action.NavigateBack -> {
                activity?.supportFragmentManager
                    ?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                activity?.viewModels<MainActivityViewModel>()?.value?.navigateToRoomsList()
            }
            is JoinRoomViewModel.Action.ShowError -> {
                Log.e("JoinRoomFragment", action.error)
                Toast.makeText(context, action.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(state: JoinRoomViewModel.State) {
        binding.apply {
            roomIdInput.visibility =
                if (state is JoinRoomViewModel.State.Loading) View.GONE else View.VISIBLE
            roomPasswordInput.visibility = roomIdInput.visibility
            joinRoomButton.visibility = roomIdInput.visibility
            joinRoomProgressBar.visibility =
                if (state is JoinRoomViewModel.State.Loading) View.VISIBLE else View.GONE
        }
    }

}