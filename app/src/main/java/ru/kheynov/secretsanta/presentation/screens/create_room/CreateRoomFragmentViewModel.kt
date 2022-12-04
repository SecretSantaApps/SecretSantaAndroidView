package ru.kheynov.secretsanta.presentation.screens.create_room

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
import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.use_cases.rooms.RoomsUseCases
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject

private const val TAG = "CreateRoomVM"

@HiltViewModel
class CreateRoomFragmentViewModel @Inject constructor(
    private val useCases: RoomsUseCases,
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    sealed interface State {
        object Loading : State
        object Idle : State
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    sealed interface Action {
        data class ShowError(val error: String) : Action
        object ShowSuccess : Action
    }

    fun createRoom(room: RoomDTO.Create) {
        viewModelScope.launch {
            _state.value = State.Loading
            when (val res = useCases.createRoomUseCase(room)) {
                is Resource.Success -> {
                    _state.value = State.Idle
                    Log.i(TAG, res.result.toString())
                    _actions.send(Action.ShowSuccess)
                }
                is Resource.Failure -> {
                    _state.value = State.Idle
                    Log.e(TAG, "Error", res.exception)
                    Action.ShowError(res.exception.cause.toString())
                }
            }
        }
    }

}