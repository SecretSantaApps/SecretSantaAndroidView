package ru.kheynov.secretsanta.presentation.screens.room_details

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentRoomDetailsBinding
import ru.kheynov.secretsanta.domain.entities.UserInfo
import ru.kheynov.secretsanta.presentation.screens.room_details.RoomDetailsViewModel.State
import ru.kheynov.secretsanta.utils.SantaException
import ru.kheynov.secretsanta.utils.dateFormatterWithoutYear
import ru.kheynov.secretsanta.utils.generateInviteLink

@AndroidEntryPoint
class RoomDetailsFragment : Fragment() {
    
    private val viewModel by viewModels<RoomDetailsViewModel>()
    
    private lateinit var binding: FragmentRoomDetailsBinding
    
    private lateinit var usersListAdapter: RoomDetailsUsersListAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRoomDetailsBinding.inflate(inflater, container, false)
        usersListAdapter = RoomDetailsUsersListAdapter(onKickUserClick = ::onKickUserClick,
            onUserLeaveClick = ::onUserLeaveClick)
        return binding.root
    }
    
    private fun onKickUserClick(user: UserInfo) {
        showKickUserDialog(binding.root, userId = user.userId)
    }
    
    private fun onUserLeaveClick() {
        showLeaveRoomDialog(binding.root)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("roomId")?.also { viewModel.setRoomId(it) }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::updateUI)
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.actions.collect(::handleAction)
            }
        }
        binding.roomUsersList.adapter = usersListAdapter
        binding.roomUsersList.layoutManager = LinearLayoutManager(context)
        binding.roomDetailsCopyLinkButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    if (state is State.Loaded) {
                        val clipboard =
                            activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val roomInfo = state.roomInfo
                        val clip: ClipData = ClipData.newPlainText("link",
                            generateInviteLink(roomId = roomInfo.id,
                                password = roomInfo.password.toString()))
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(activity, "Copied!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    private fun updateUI(state: State) {
        binding.bindErrorScreen(state)
        binding.apply {
            roomOwner.visibility = if (state is State.Loading) View.INVISIBLE else View.VISIBLE
            roomDate.visibility = roomOwner.visibility
            roomMaxPrice.visibility = roomOwner.visibility
            roomRecipient.visibility = roomOwner.visibility
            roomPlayersTitle.visibility = roomOwner.visibility
            startStopGameButton.visibility = roomOwner.visibility
            roomPassword.visibility = roomOwner.visibility
            roomId.visibility = roomOwner.visibility
            roomUsersList.visibility = roomOwner.visibility
            roomDetailsCopyLinkButton.visibility = roomOwner.visibility
            roomDetailsProgressBar.visibility =
                if (state is State.Loading) View.VISIBLE else View.GONE
            
            if (state is State.Loaded) {
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
                            roomDate.text = getString(R.string.room_deadline_placeholder, it)
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
                            startStopGameButton.visibility = View.GONE
                            startStopGameButton.isEnabled = false
                            roomDetailsCopyLinkButton.visibility = View.INVISIBLE
                        } else {
                            roomPassword.visibility = View.VISIBLE
                            startStopGameButton.visibility = View.VISIBLE
                            roomDetailsCopyLinkButton.visibility = View.VISIBLE
                            startStopGameButton.isEnabled = true
                            roomPassword.text = getString(R.string.room_password_placeholder, it)
                            usersListAdapter.enableBlocking()
                        }
                    }
                }
            }
        }
    }
    
    private fun FragmentRoomDetailsBinding.bindErrorScreen(state: State) {
        apply {
            errorLayout.visibility = when (state) {
                is State.Error -> View.VISIBLE
                else -> View.GONE
            }
            errorImage.apply {
                setImageDrawable(when (state) {
                    is State.Error -> if (state.error is SantaException) {
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warn)
                    } else AppCompatResources.getDrawable(requireContext(),
                        R.drawable.ic_network_error)
                    else -> {
                        null
                    }
                })
                
            }
            errorMessageDev.text = when (state) {
                is State.Error -> {
                    getString(R.string.message_for_devs_placeholder, state.error.toString())
                }
                else -> ""
            }
            errorMessage.text = when (state) {
                is State.Error -> {
                    if (state.error is SantaException) {
                        getString(R.string.santa_exception_placeholder,
                            state.error.javaClass.simpleName.toString())
                    } else getString(R.string.internet_connection_error)
                }
                else -> ""
            }
            retryButton.apply {
                visibility = if (state is State.Error) View.VISIBLE
                else View.INVISIBLE
                setOnClickListener {
                    viewModel.loadInfo()
                }
            }
            
        }
    }
    
    
    private fun handleAction(action: RoomDetailsViewModel.Action) {
        when (action) {
            is RoomDetailsViewModel.Action.ShowError -> {
                with(action.error.getText(requireContext())) {
                    if (contains("NotEnoughPlayers")) {
                        Toast.makeText(activity,
                            "Error: ${getString(R.string.not_enough_players)}",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity,
                            "Error: ${action.error.getText(requireContext())}",
                            Toast
                                .LENGTH_SHORT)
                            .show()
                    }
                }
            }
            RoomDetailsViewModel.Action.NavigateBack -> activity?.supportFragmentManager?.popBackStack()
        }
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadInfo()
    }
    
    private fun showLeaveRoomDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.apply {
            setTitle(getString(R.string.leave_dialog))
            setMessage(getString(R.string.leave_confirmation))
            setPositiveButton(getString(R.string.leave_dialog_button)) { dialog, _ ->
                dialog.dismiss()
                viewModel.leaveRoom()
            }
            
            setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
    
    private fun showKickUserDialog(view: View, userId: String) {
        val builder = AlertDialog.Builder(view.context)
        builder.apply {
            setTitle(getString(R.string.kick_dialog))
            setMessage(getString(R.string.kick_confirmation))
            setPositiveButton(getString(R.string.kick_dialog_button)) { dialog, _ ->
                dialog.dismiss()
                viewModel.kickUser(userId)
            }
            
            setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }
    
}