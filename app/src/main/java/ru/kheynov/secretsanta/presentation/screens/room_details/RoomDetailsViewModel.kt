package ru.kheynov.secretsanta.presentation.screens.room_details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.domain.entities.GameDTO
import ru.kheynov.secretsanta.domain.use_cases.game.GameUseCases
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject

private const val TAG = "RoomsListViewModel"

@HiltViewModel
class RoomDetailsViewModel @Inject constructor(
    private val gameUseCases: GameUseCases,
    private val userUseCases: UsersUseCases,
) : ViewModel() {
    sealed interface Action {
        data class ShowError(val error: String) : Action
        object NavigateBack : Action
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val roomInfo: GameDTO.RoomInfo, val userId: String) : State
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private var roomId: String? = null

    fun setRoomId(roomId: String) {
        this.roomId = roomId
    }

    fun loadInfo() {
        viewModelScope.launch {
            _state.value = State.Loading
            if (roomId.isNullOrBlank()) {
                _actions.send(Action.ShowError("Room name cannot be null"))
                return@launch
            }
            val userId = (userUseCases.getSelfInfoUseCase() as Resource.Success).result.userId
            //TODO: rewrite
            Log.i(TAG, "roomId: $roomId")
            when (val res = gameUseCases.getGameInfoUseCase(
                roomId!!
            )) {
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result, userId)
                }
                is Resource.Failure -> {
                    Log.e(TAG, "Something went wrong", res.exception)
                    _actions.send(Action.ShowError(res.exception.message.toString()))
                }
            }
        }
    }

    fun startGame() {
        viewModelScope.launch {
            if (roomId.isNullOrBlank()) {
                _actions.send(Action.ShowError("Room ID cannot be null"))
                return@launch
            }
            when (val res = gameUseCases.startGameUseCase(roomId!!)) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.javaClass.simpleName.toString()))
                }
                is Resource.Success -> loadInfo()
            }
        }
    }

    fun stopGame() {
        viewModelScope.launch {
            if (roomId.isNullOrBlank()) {
                _actions.send(Action.ShowError("Room ID cannot be null"))
                return@launch
            }
            when (val res = gameUseCases.stopGameUseCase(roomId!!)) {
                is Resource.Failure -> _actions.send(Action.ShowError(res.exception.toString()))
                is Resource.Success -> loadInfo()
            }
        }
    }

    fun kickUser(user: String) {
        viewModelScope.launch {
            val roomId = (_state.value as State.Loaded).roomInfo.id
            _state.value = State.Loading
            when (val res = gameUseCases.kickUserUseCase(GameDTO.KickUser(user, roomId))) {
                is Resource.Success -> {
                    loadInfo()
                }
                is Resource.Failure -> _actions.send(Action.ShowError(res.exception.toString()))
            }
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            val roomId = (_state.value as State.Loaded).roomInfo.id
            _state.value = State.Loading
            when (val res = gameUseCases.leaveGameUseCase(roomId)) {
                is Resource.Success -> {
                    _actions.send(Action.NavigateBack)
                }
                is Resource.Failure -> _actions.send(Action.ShowError(res.exception.toString()))
            }
        }
    }
}