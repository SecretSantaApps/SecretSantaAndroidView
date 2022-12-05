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

    private var roomName: String? = null

    fun setRoomName(roomName: String) {
        this.roomName = roomName
    }

    fun loadInfo() {
        viewModelScope.launch {
            _state.value = State.Loading
            if (roomName == null) {
                _actions.send(Action.ShowError("Room name cannot be null"))
                return@launch
            }
            val userId = (userUseCases.getSelfInfoUseCase() as Resource.Success).result.userId
            //TODO: rewrite
            when (val res = gameUseCases.getGameInfoUseCase(
                GameDTO.GetRoomInfo(roomName.toString())
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
            when (val res = gameUseCases.startGameUseCase(GameDTO.Start(roomName ?: run {
                _actions.send(Action.ShowError("Room name is null"))
                return@launch
            }))) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.javaClass.simpleName.toString()))
                }
                is Resource.Success -> loadInfo()
            }
        }
    }

    fun stopGame() {
        viewModelScope.launch {
            when (val res = gameUseCases.stopGameUseCase(
                GameDTO.Stop(roomName ?: run {
                    _actions.send(Action.ShowError("Room name is null"))
                    return@launch
                })
            )) {
                is Resource.Failure -> _actions.send(Action.ShowError(res.exception.toString()))
                is Resource.Success -> loadInfo()
            }
        }
    }

    fun kickUser(user: String) {
        viewModelScope.launch {
            val roomName = (_state.value as State.Loaded).roomInfo.roomName
            _state.value = State.Loading
            when (val res = gameUseCases.kickUserUseCase(GameDTO.KickUser(user, roomName))) {
                is Resource.Success -> {
                    loadInfo()
                }
                is Resource.Failure -> _actions.send(Action.ShowError(res.exception.toString()))
            }
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            val roomName = (_state.value as State.Loaded).roomInfo.roomName
            _state.value = State.Loading
            when (val res = gameUseCases.leaveGameUseCase(GameDTO.Leave(roomName))) {
                is Resource.Success -> {
                    _actions.send(Action.NavigateBack)
                }
                is Resource.Failure -> _actions.send(Action.ShowError(res.exception.toString()))
            }
        }
    }
}