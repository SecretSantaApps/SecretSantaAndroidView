package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentRoomsListBinding
import ru.kheynov.secretsanta.domain.entities.RoomItem
import ru.kheynov.secretsanta.presentation.screens.join_room.JoinRoomFragment
import ru.kheynov.secretsanta.presentation.screens.room_details.RoomDetailsFragment
import ru.kheynov.secretsanta.utils.SantaException
import ru.kheynov.secretsanta.utils.dateFormatterWithoutYear
import ru.kheynov.secretsanta.utils.navigateToLoginScreen
import java.time.LocalDate

@AndroidEntryPoint
class RoomsListFragment : Fragment() {
    private lateinit var binding: FragmentRoomsListBinding
    
    private val viewModel by viewModels<RoomsListViewModel>()
    private lateinit var roomsListAdapter: RoomsListAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRoomsListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        roomsListAdapter = RoomsListAdapter(::onRoomClick)
        binding.apply {
            roomsList.adapter = roomsListAdapter
            roomsList.layoutManager = LinearLayoutManager(context)
            joinRoomButton.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, JoinRoomFragment())?.addToBackStack(null)
                    ?.commit()
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rooms.collect {
                    val rooms = it.map { room ->
                        RoomItem(roomId = room.roomId,
                            roomName = room.roomName,
                            membersCount = getString(R.string.members_count_placeholder,
                                room.membersCount),
                            gameState = with(room.gameState) {
                                if (this == "false") getString(R.string.waiting_for_start)
                                else getString(R.string.game_started_text)
                            },
                            date = if (room.date != "null") getString(R.string.deadline_placeholder,
                                LocalDate.parse(room.date)
                                    .format(dateFormatterWithoutYear)) else room.date,
                            gameStateColor = if (room.gameState == "false") ContextCompat.getColor(
                                requireContext(),
                                R.color.colorAccent)
                            else ContextCompat.getColor(requireContext(), R.color.green))
                    }
                    roomsListAdapter.updateRoomsList(rooms)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.apply {
                        roomsListProgressBar.visibility =
                            if (state is RoomsListViewModel.State.Loading) View.VISIBLE else View.GONE
                        roomsList.visibility =
                            if (state is RoomsListViewModel.State.Idle) View.VISIBLE else View.GONE
                        
                        bindErrorScreen(state)
                    }
                }
            }
        }
    }
    
    private fun FragmentRoomsListBinding.bindErrorScreen(state: RoomsListViewModel.State) {
        apply {
            errorLayout.visibility = when (state) {
                is RoomsListViewModel.State.Error -> View.VISIBLE
                RoomsListViewModel.State.Idle -> if (viewModel.rooms.value.isEmpty()) {
                    View.VISIBLE
                } else View.GONE
                RoomsListViewModel.State.Loading -> View.GONE
            }
            errorImage.apply {
                setImageDrawable(when (state) {
                    is RoomsListViewModel.State.Error -> if (state.error is SantaException) {
                        
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warn)
                    } else AppCompatResources.getDrawable(requireContext(),
                        R.drawable.ic_network_error)
                    RoomsListViewModel.State.Idle,
                    -> if (viewModel.rooms.value.isEmpty()) {
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_empty_list)
                    } else null
                    RoomsListViewModel.State.Loading -> {
                        null
                    }
                })
                
            }
            errorMessageDev.text = when (state) {
                is RoomsListViewModel.State.Error -> {
                    getString(R.string.message_for_devs_placeholder, state.error.toString())
                }
                else -> ""
            }
            errorMessage.text = when (state) {
                is RoomsListViewModel.State.Error -> {
                    if (state.error is SantaException) {
                        getString(R.string.santa_exception_placeholder,
                            state.error.javaClass.simpleName.toString())
                    } else getString(R.string.internet_connection_error)
                }
                
                is RoomsListViewModel.State.Idle -> {
                    if (viewModel.rooms.value.isEmpty()) getString(R.string.empty_rooms_list_error)
                    else ""
                }
                else -> ""
            }
            retryButton.apply {
                visibility = if (state is RoomsListViewModel.State.Error) View.VISIBLE
                else View.INVISIBLE
                setOnClickListener {
                    viewModel.loadRooms()
                }
            }
            
        }
    }
    
    private fun onRoomClick(room: RoomItem) {
        val fragment = RoomDetailsFragment()
        val args = Bundle().apply {
            putString("roomId", room.roomId)
        }
        fragment.arguments = args
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)?.addToBackStack(null)?.commit() ?: Log.i(
            tag,
            "Unable to navigate")
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadRooms()
        lifecycleScope.launch {
            viewModel.actions.collect {
                handleAction(it, requireContext())
            }
        }
    }
    
    private fun handleAction(action: RoomsListViewModel.Action, context: Context) {
        when (action) {
            RoomsListViewModel.Action.RouteToLogin -> navigateToLoginScreen(context)
        }
    }
    
}