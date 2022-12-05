package ru.kheynov.secretsanta.presentation.screens.room_details

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
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentRoomDetailsBinding
import ru.kheynov.secretsanta.utils.dateFormatterWithoutYear

@AndroidEntryPoint
class RoomDetailsFragment : Fragment() {

    private val viewModel by viewModels<RoomDetailsViewModel>()

    private lateinit var binding: FragmentRoomDetailsBinding

    private val args: RoomDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoomDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setRoomName(args.roomName)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::updateUI)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.actions.collect(::handleAction)
            }
        }
    }

    private fun updateUI(state: RoomDetailsViewModel.State) {
        binding.apply {
            roomOwner.visibility = if (state is RoomDetailsViewModel.State.Loading) View
                .GONE else View.VISIBLE
            roomDate.visibility = if (state is RoomDetailsViewModel.State.Loading) View
                .GONE else View.VISIBLE
            roomMaxPrice.visibility = if (state is RoomDetailsViewModel.State.Loading) View
                .GONE else View.VISIBLE
            roomRecipient.visibility = if (state is RoomDetailsViewModel.State.Loading) View
                .GONE else View.VISIBLE
            roomPlayersTitle.visibility = if (state is RoomDetailsViewModel.State.Loading) View
                .GONE else View.VISIBLE
            roomDetailsProgressBar.visibility =
                if (state is RoomDetailsViewModel.State.Loading) View
                    .VISIBLE else View.GONE
            joinUserButton.visibility = if (state is RoomDetailsViewModel.State.Loading) View
                .GONE else View.VISIBLE

            if (state is RoomDetailsViewModel.State.Loaded) {
                roomDetailsName.text = state.roomInfo.roomName
                roomOwner.text = getString(
                    R.string.room_owner_placeholder, state.roomInfo.users
                        .find {
                            it.userId == state.roomInfo
                                .ownerId
                        }?.username
                )
                roomDate.text =
                    getString(R.string.room_date_placeholder,
                        state.roomInfo.date?.format(dateFormatterWithoutYear) ?: "".also {
                            roomDate.visibility = View.GONE
                        })
                state.roomInfo.max_price.also {
                    if (it == null || this.toString().isBlank()) {
                        roomMaxPrice.visibility = View.GONE
                    } else {
                        roomMaxPrice.text = getString(R.string.room_max_price_placeholder, it)
                        roomMaxPrice.visibility = View.VISIBLE
                    }
                }

                state.roomInfo.users.find {
                    it.userId == state.roomInfo
                        .recipient
                }?.username.also {
                    if (it == null) {
                        roomRecipient.visibility = View.GONE
                    } else {
                        roomRecipient.text = it
                        roomRecipient.visibility = View.VISIBLE
                    }
                }
                state.roomInfo.password.also {
                    if (it == null) {
                        roomPassword.visibility = View.GONE
                    } else {
                        roomPassword.visibility = View.VISIBLE
                        roomPassword.text = getString(R.string.room_password_placeholder, it)
                    }
                }
            }

        }
    }

    private fun handleAction(action: RoomDetailsViewModel.Action) {
        when (action) {
            is RoomDetailsViewModel.Action.ShowError -> {
                Toast.makeText(activity, "Error: ${action.error}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInfo()
    }

}