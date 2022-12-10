package ru.kheynov.secretsanta.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.domain.entities.UpdateUser
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.UiText
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val useCases: UsersUseCases,
) : ViewModel() {
    
    sealed interface State {
        object Loading : State
        data class Loaded(val username: String) : State
    }
    
    sealed interface Action {
        object NavigateBack : Action
        data class ShowError(val error: UiText) : Action
    }
    
    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state
    
    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    
    private val ioDispatcher = Dispatchers.IO
    
    init {
        viewModelScope.launch {
            val res = withContext(ioDispatcher) {
                useCases.getSelfInfoUseCase()
            }
            when (res) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(when (val e = res.exception) {
                        is UserNotExistsException -> UiText.StringResource(R.string.user_not_exists_error)
                        else -> UiText.PlainText(e.javaClass.simpleName.toString())
                    }))
                    delay(1000)
                    _actions.send(Action.NavigateBack)
                }
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result.username)
                }
            }
        }
    }
    
    fun saveUsername(name: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            with(name) {
                if (isBlank()) {
                    _actions.send(Action.ShowError(UiText.StringResource(R.string.name_blank_error)))
                    _actions.send(Action.NavigateBack)
                    return@launch
                }
                if (length > 15) {
                    _actions.send(Action.ShowError(UiText.StringResource(R.string.name_too_long)))
                    _actions.send(Action.NavigateBack)
                    return@launch
                }
            }
            val res = withContext(ioDispatcher) {
                useCases.updateUserUseCase(updateUser = UpdateUser(username = name))
            }
            when (res) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(UiText.PlainText(res.exception.toString())))
                }
                is Resource.Success -> _actions.send(Action.NavigateBack)
            }
        }
    }
}