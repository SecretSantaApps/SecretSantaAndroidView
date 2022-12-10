package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        data class Error(val error: Exception) : State
    }
    
    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    
    sealed interface Action {
        object RouteToLogin : Action
    }
    
    private val _rooms = MutableStateFlow(listOf<RoomItem>())
    val rooms: StateFlow<List<RoomItem>> = _rooms
    
    private var job: Job? = null
    
    private val ioDispatcher = Dispatchers.IO
    private val mainDispatcher = Dispatchers.Main
    private val defaultDispatcher = Dispatchers.Default
    
    fun loadRooms() {
        job = CoroutineScope(ioDispatcher).launch {
            _state.value = State.Loading
            val res = useCases.getRoomsListUseCase()
            withContext(mainDispatcher) {
                when (res) {
                    is Resource.Success -> {
                        _rooms.value = withContext(defaultDispatcher) {
                            res.result.map {
                                RoomItem(
                                    roomId = it.id,
                                    roomName = it.name,
                                    membersCount = it.membersCount.toString(),
                                    gameState = it.gameStarted.toString(),
                                    date = it.date.toString(),
                                    gameStateColor = 0
                                )
                            }
                        }
                        _state.value = State.Idle
                    }
                    is Resource.Failure -> {
                        _state.value = State.Idle
                        when (res.exception) {
                            is CancellationException -> {
                                Log.d(TAG, "loadRooms: CancellationException")
                            }
                            is UserNotExistsException -> {
                                _actions.send(Action.RouteToLogin)
                            }
                            else -> {
                                _state.value = State.Error(res.exception)
                            }
                        }
                    }
                }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}