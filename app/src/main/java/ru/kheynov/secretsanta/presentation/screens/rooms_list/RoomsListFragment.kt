package ru.kheynov.secretsanta.presentation.screens.rooms_list

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.databinding.FragmentRoomsListBinding
import ru.kheynov.secretsanta.domain.entities.RoomItem
import ru.kheynov.secretsanta.presentation.screens.join_room.JoinRoomFragment
import ru.kheynov.secretsanta.presentation.screens.room_details.RoomDetailsFragment
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
                    ?.replace(R.id.fragment_container, JoinRoomFragment())
                    ?.addToBackStack("")
                    ?.commit()
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rooms.collect {
                    val rooms = it.map { room ->
                        RoomItem(
                            roomId = room.roomId,
                            roomName = room.roomName,
                            membersCount = getString(
                                R.string.members_count_placeholder, room.membersCount
                            ),
                            gameState = with(room.gameState) {
                                if (this == "false") getString(R.string.waiting_for_start)
                                else getString(R.string.game_started_text)
                            },
                            date = if (room.date != "null") getString(
                                R.string.deadline_placeholder, LocalDate.parse(
                                    room.date
                                ).format(dateFormatterWithoutYear)
                            ) else room.date,
                            gameStateColor = if (room.gameState == "false") context?.getColor(
                                R.color.colorAccent
                            ) else context?.getColor(R.color.green)
                        )
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
                    }
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
            ?.replace(R.id.fragment_container, fragment)?.addToBackStack("")?.commit() ?: Log.i(
            tag, "Unable to navigate"
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRooms()
        lifecycleScope.launch {
            viewModel.actions.collect {
                handleAction(it, context!!)
            }
        }
    }

    private fun handleAction(action: RoomsListViewModel.Action, context: Context) {
        when (action) {
            RoomsListViewModel.Action.RouteToLogin -> navigateToLoginScreen(context)
            is RoomsListViewModel.Action.ShowError -> {
                Log.e(tag, action.error)
                Toast.makeText(
                    context, action.error, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}