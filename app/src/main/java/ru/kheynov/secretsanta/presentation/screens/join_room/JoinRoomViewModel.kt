package ru.kheynov.secretsanta.presentation.screens.join_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.domain.use_cases.game.GameUseCases
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.SantaException
import javax.inject.Inject

@HiltViewModel
class JoinRoomViewModel @Inject constructor(
    private val gameUseCases: GameUseCases,
) : ViewModel() {

    sealed interface State {
        object Loading : State
        object Idle : State
        data class Error(val exception: SantaException) : State
    }

    sealed interface Action {
        data class ShowError(val error: String) : Action
        object NavigateBack : Action
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    fun joinRoom(roomId: String, password: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            if (roomId.isBlank()) {
                _actions.send(Action.ShowError("Room ID can't be empty"))
                _state.value = State.Idle
                return@launch
            }
            when (val res = gameUseCases.joinGameUseCase(roomId, password)) {
                is Resource.Success -> {
                    _actions.send(Action.NavigateBack)
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
}