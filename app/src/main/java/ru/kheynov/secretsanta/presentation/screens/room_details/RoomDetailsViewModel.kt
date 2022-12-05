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
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject

private const val TAG = "RoomsListViewModel"

@HiltViewModel
class RoomDetailsViewModel @Inject constructor(
    private val gameUseCases: GameUseCases,
) : ViewModel() {
    sealed interface Action {
        data class ShowError(val error: String) : Action
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val roomInfo: GameDTO.RoomInfo) : State
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
            if (roomName == null) {
                _actions.send(Action.ShowError("Room name cannot be null"))
                return@launch
            }
            when (val res = gameUseCases.getGameInfoUseCase(
                GameDTO.GetRoomInfo(roomName.toString())
            )) {
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result)
                }
                is Resource.Failure -> {
                    Log.e(TAG, "Something went wrong", res.exception)
                    _actions.send(Action.ShowError(res.exception.message.toString()))
                }
            }
        }
    }
}