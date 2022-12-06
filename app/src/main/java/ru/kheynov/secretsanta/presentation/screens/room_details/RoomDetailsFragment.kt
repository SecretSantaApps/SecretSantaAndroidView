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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentRoomDetailsBinding
import ru.kheynov.secretsanta.domain.entities.UserInfo
import ru.kheynov.secretsanta.utils.dateFormatterWithoutYear

@AndroidEntryPoint
class RoomDetailsFragment : Fragment() {

    private val viewModel by viewModels<RoomDetailsViewModel>()

    private lateinit var binding: FragmentRoomDetailsBinding

    private val args: RoomDetailsFragmentArgs by navArgs()

    private lateinit var usersListAdapter: RoomDetailsUsersListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoomDetailsBinding.inflate(inflater, container, false)
        usersListAdapter = RoomDetailsUsersListAdapter(
            onKickUserClick = ::onKickUserClick, onUserLeaveClick = ::onUserLeaveClick
        )
        return binding.root
    }

    private fun onKickUserClick(user: UserInfo) {
        viewModel.kickUser(user.userId)//TODO: add alert dialog
    }

    private fun onUserLeaveClick() {
        viewModel.leaveRoom()//TODO: add alert dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setRoomId(args.roomId)
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
        binding.roomUsersList.adapter = usersListAdapter
        binding.roomUsersList.layoutManager = LinearLayoutManager(context)
    }

    private fun updateUI(state: RoomDetailsViewModel.State) {
        binding.apply {
            roomOwner.visibility =
                if (state is RoomDetailsViewModel.State.Loading) View.GONE else View.VISIBLE
            roomDate.visibility = roomOwner.visibility
            roomMaxPrice.visibility = roomOwner.visibility
            roomRecipient.visibility = roomOwner.visibility
            roomPlayersTitle.visibility = roomOwner.visibility
            startStopGameButton.visibility = roomOwner.visibility
            roomPassword.visibility = roomOwner.visibility
            roomId.visibility = roomOwner.visibility
            roomUsersList.visibility = roomOwner.visibility
            roomDetailsProgressBar.visibility =
                if (state is RoomDetailsViewModel.State.Loading) View.VISIBLE else View.GONE

            if (state is RoomDetailsViewModel.State.Loaded) {
                usersListAdapter.updateList(state.roomInfo.users)
                usersListAdapter.setOwnerId(state.userId)
                state.roomInfo.apply {
                    roomDetailsName.text = roomName
                    roomId.text = getString(R.string.room_id_placeholder, id)
                    users.find {
                        it.userId == ownerId
                    }?.username.also {
                        roomOwner.text = getString(R.string.room_owner_placeholder, it)
                    }
                    date?.format(dateFormatterWithoutYear).also {
                        if (it == null) {
                            roomDate.visibility = View.GONE
                        } else {
                            roomDate.visibility = View.VISIBLE
                            roomDate.text = getString(R.string.room_date_placeholder, it)
                        }
                    }
                    max_price.also {
                        if (it == null || this.toString().isBlank()) {
                            roomMaxPrice.visibility = View.GONE
                        } else {
                            roomMaxPrice.text = getString(R.string.room_max_price_placeholder, it)
                            roomMaxPrice.visibility = View.VISIBLE
                        }
                    }
                    users.find {
                        it.userId == recipient
                    }?.username.also {
                        if (it == null) {
                            roomRecipient.visibility = View.GONE
                            startStopGameButton.text = getString(R.string.start_game)
                            startStopGameButton.setOnClickListener {
                                viewModel.startGame()
                            }
                        } else {
                            roomRecipient.text = getString(R.string.room_recipient_placeholder, it)
                            roomRecipient.visibility = View.VISIBLE
                            startStopGameButton.text = getString(R.string.stop_game)
                            startStopGameButton.setOnClickListener {
                                viewModel.stopGame()
                            }
                        }
                    }
                    password.also {
                        if (it == null) {
                            roomPassword.visibility = View.GONE
                            startStopGameButton.visibility = View.INVISIBLE
                            startStopGameButton.isEnabled = false
                        } else {
                            roomPassword.visibility = View.VISIBLE
                            startStopGameButton.visibility = View.VISIBLE
                            startStopGameButton.isEnabled = true
                            roomPassword.text = getString(R.string.room_password_placeholder, it)
                            usersListAdapter.enableBlocking()
                        }
                    }
                }
            }
        }
    }

    private fun handleAction(action: RoomDetailsViewModel.Action) {
        when (action) {
            is RoomDetailsViewModel.Action.ShowError -> {
                with(action.error) {
                    if (contains("NotEnoughPlayers")) {
                        Toast.makeText(
                            activity,
                            "Error: ${getString(R.string.not_enough_players)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            activity, "Error: ${action.error}", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            RoomDetailsViewModel.Action.NavigateBack -> findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInfo()
    }

}