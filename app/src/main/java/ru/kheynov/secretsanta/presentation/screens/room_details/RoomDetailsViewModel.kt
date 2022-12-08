package ru.kheynov.secretsanta.presentation.screens.room_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.kheynov.secretsanta.domain.entities.GameDTO
import ru.kheynov.secretsanta.domain.use_cases.game.GameUseCases
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.SantaException
import javax.inject.Inject

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
        data class Error(val error: Exception) : State
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
                _state.value = State.Error(Exception("Room id cannot be null"))
                return@launch
            }
            val userId = (userUseCases.getSelfInfoUseCase() as Resource.Success).result.userId
            when (val res = gameUseCases.getGameInfoUseCase(roomId!!)) {
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result, userId)
                }
                is Resource.Failure -> {
                    if (res.exception is SantaException || res.exception is HttpException) {
                        _state.value = State.Error(res.exception)
                    } else {
                        _actions.send(Action.ShowError(res.exception.message.toString()))
                    }
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
            val lastState = _state.value
            _state.value = State.Loading
            when (val res = gameUseCases.startGameUseCase(roomId!!)) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.javaClass.simpleName.toString()))
                    _state.value = lastState
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
            val lastState = _state.value
            when (val res = gameUseCases.stopGameUseCase(roomId!!)) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.javaClass.simpleName.toString()))
                    _state.value = lastState
                }
                is Resource.Success -> loadInfo()
            }
        }
    }

    fun kickUser(user: String) {
        viewModelScope.launch {
            val roomId = (_state.value as State.Loaded).roomInfo.id
            val lastState = _state.value
            _state.value = State.Loading
            when (val res = gameUseCases.kickUserUseCase(GameDTO.KickUser(user, roomId))) {
                is Resource.Success -> {
                    loadInfo()
                }
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.toString()))
                    _state.value = lastState
                }
            }
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            val roomId = (_state.value as State.Loaded).roomInfo.id
            val lastState = _state.value
            _state.value = State.Loading
            when (val res = gameUseCases.leaveGameUseCase(roomId)) {
                is Resource.Success -> {
                    _actions.send(Action.NavigateBack)
                }
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.toString()))
                    _state.value = lastState
                }
            }
        }
    }
}