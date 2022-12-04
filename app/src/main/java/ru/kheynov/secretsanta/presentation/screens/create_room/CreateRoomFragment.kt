package ru.kheynov.secretsanta.presentation.screens.create_room

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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentCreateRoomBinding
import ru.kheynov.secretsanta.domain.entities.RoomDTO

@AndroidEntryPoint
class CreateRoomFragment : Fragment() {

    private lateinit var binding: FragmentCreateRoomBinding

    private val viewModel by viewModels<CreateRoomFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        Log.i(tag, "Started")
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
            createRoomButton.setOnClickListener {
                val room = RoomDTO.Create(
                    roomName = with(roomNameInput.text) {
                        if (toString().isBlank()) return@setOnClickListener
                        toString()
                    },
                    password = roomPasswordInput.text.toString(),
                    date = null,
                    maxPrice = with(roomMaxPriceInput.text.toString()) {
                        if (isNullOrBlank()) null
                        else toInt()
                    }
                )
                viewModel.createRoom(room)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
    }

    private fun updateUI(state: CreateRoomFragmentViewModel.State) {
        binding.apply {
            createRoomButton.visibility =
                if (state is CreateRoomFragmentViewModel.State.Idle) View.VISIBLE else View.GONE
            roomNameInput.visibility =
                if (state is CreateRoomFragmentViewModel.State.Idle) View.VISIBLE else View.GONE
            roomPasswordInput.visibility =
                if (state is CreateRoomFragmentViewModel.State.Idle) View.VISIBLE else View.GONE
            roomMaxPriceInput.visibility =
                if (state is CreateRoomFragmentViewModel.State.Idle) View.VISIBLE else View.GONE
            pickDeadlineDate.visibility =
                if (state is CreateRoomFragmentViewModel.State.Idle) View.VISIBLE else View.GONE
            createRoomProgressBar.visibility =
                if (state is CreateRoomFragmentViewModel.State.Loading) View.VISIBLE else View.GONE
        }
    }

    private fun handleAction(action: CreateRoomFragmentViewModel.Action) {
        when (action) {
            is CreateRoomFragmentViewModel.Action.ShowError -> Toast.makeText(
                activity, "Error: ${action.error}", Toast.LENGTH_SHORT
            ).show()
            CreateRoomFragmentViewModel.Action.ShowSuccess -> {
                findNavController().navigate(R.id.createdSuccessfully)
            }
        }
    }
}