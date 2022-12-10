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
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.domain.use_cases.game.GameUseCases
import ru.kheynov.secretsanta.utils.GameAlreadyStartedException
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.RoomNotExistsException
import ru.kheynov.secretsanta.utils.SantaException
import ru.kheynov.secretsanta.utils.UiText
import ru.kheynov.secretsanta.utils.UserAlreadyInRoomException
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

@HiltViewModel
class JoinRoomViewModel @Inject constructor(
    private val gameUseCases: GameUseCases,
) : ViewModel() {
    
    sealed interface State {
        object Loading : State
        object Idle : State
    }
    
    sealed interface Action {
        data class ShowError(val error: UiText) : Action
        object NavigateBack : Action
    }
    
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state
    
    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    
    fun joinRoom(roomId: String, password: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            
            if (roomId.isBlank() || password.isBlank()) {
                _actions.send(Action.ShowError(UiText.StringResource(R.string.room_id_or_password_empty_error)))
                _state.value = State.Idle
                return@launch
            }
            if (roomId.length < 6 || roomId.length > 8) {
                _actions.send(Action.ShowError(UiText.StringResource(R.string.wrong_credentials_format)))
                _state.value = State.Idle
                return@launch
            }
            
            when (val res = gameUseCases.joinGameUseCase(roomId, password)) {
                is Resource.Success -> {
                    _actions.send(Action.NavigateBack)
                }
                is Resource.Failure -> {
                    _state.value = State.Idle
                    if (res.exception is SantaException) {
                        _actions.send(Action.ShowError(when (val e = res.exception) {
                            is GameAlreadyStartedException -> UiText.StringResource(R.string.game_already_started_error)
                            is UserAlreadyInRoomException -> UiText.StringResource(R.string.user_already_in_room_exception)
                            is RoomNotExistsException -> UiText.StringResource(R.string.room_not_exists_error)
                            is UserNotExistsException -> UiText.StringResource(R.string.user_not_exists_error)
                            else -> UiText.PlainText(e.javaClass.simpleName.toString())
                        }))
                    } else {
                        _actions.send(Action.ShowError(UiText.PlainText(res.exception.javaClass.simpleName.toString())))
                    }
                }
            }
        }
    }
}