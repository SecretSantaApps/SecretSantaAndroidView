package ru.kheynov.secretsanta.presentation.screens.create_room

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
import ru.kheynov.secretsanta.utils.SantaException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateRoomFragmentViewModel @Inject constructor(
    private val useCases: RoomsUseCases,
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    sealed interface State {
        object Loading : State
        object Idle : State
        data class Loaded(val room: RoomDTO.Info) : State
        data class Error(val exception: SantaException) : State
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    sealed interface Action {
        data class ShowError(val error: String) : Action
        object ShowSuccess : Action
    }

    private var _date: MutableStateFlow<LocalDate?> = MutableStateFlow(null)
    val date: StateFlow<LocalDate?> = _date

    fun createRoom(room: RoomDTO.Create) {
        viewModelScope.launch {
            _state.value = State.Loading
            if (room.roomName.isBlank()) {
                _actions.send(Action.ShowError("Room name can't be empty"))
                _state.value = State.Idle
                return@launch
            }
            when (val res = useCases.createRoomUseCase(room)) {
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result)
                    _actions.send(Action.ShowSuccess)
                }
                is Resource.Failure -> {
                    if (res.exception is SantaException) {
                        _state.value = State.Error(res.exception)
                    } else {
                        _state.value = State.Idle
                        _actions.send(Action.ShowError(res.exception.javaClass.simpleName.toString()))
                    }
                }
            }
        }
    }

    fun setDate(localDate: LocalDate?) {
        _date.value = localDate
    }

    fun clearDate() {
        _date.value = null
    }
}