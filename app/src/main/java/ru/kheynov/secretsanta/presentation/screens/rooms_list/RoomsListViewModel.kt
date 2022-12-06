package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.domain.entities.RoomItem
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

private const val TAG = "RoomsListViewModel"

@HiltViewModel
class RoomsListViewModel @Inject constructor(
    private val useCases: UsersUseCases,
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    sealed interface State {
        object Loading : State
        object Idle : State
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    sealed interface Action {
        data class ShowError(val error: String) : Action
        object RouteToLogin : Action
    }

    private val _rooms = MutableStateFlow(listOf<RoomItem>())
    val rooms: StateFlow<List<RoomItem>> = _rooms

    fun loadRooms() {
        viewModelScope.launch {
            _state.value = State.Loading
            when (val result = useCases.getRoomsListUseCase()) {
                is Resource.Success -> {
                    _rooms.value = result.result.map {
                        RoomItem(
                            roomId = it.id,
                            roomName = it.name,
                            membersCount = it.membersCount.toString(),
                            gameState = it.gameStarted.toString(),
                            date = it.date.toString(),
                            gameStateColor = 0
                        )
                    }
                    _state.value = State.Idle
                }
                is Resource.Failure -> {
                    when (result.exception) {
                        is CancellationException -> {
                            Log.d(TAG, "loadRooms: CancellationException")
                        }
                        is UserNotExistsException -> {
                            _actions.send(Action.RouteToLogin)
                        }
                        else -> {
                            _actions.send(
                                Action.ShowError(
                                    result.exception.message ?: "Unknown error"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}