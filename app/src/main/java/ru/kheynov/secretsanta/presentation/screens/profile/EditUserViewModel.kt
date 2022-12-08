package ru.kheynov.secretsanta.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.domain.entities.UpdateUser
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val useCases: UsersUseCases,
) : ViewModel() {

    sealed interface State {
        object Loading : State
        data class Loaded(val username: String) : State
        data class Error(val error: Exception) : State
    }

    sealed interface Action {
        object NavigateBack : Action
        data class ShowError(val error: String) : Action
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    init {
        viewModelScope.launch {
            when (val res = useCases.getSelfInfoUseCase()) {
                is Resource.Failure -> {
                    _state.value = State.Error(res.exception)
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
                    _actions.send(Action.ShowError("Name couldn't be blank"))
                }
                if (length > 15) {
                    _actions.send(Action.ShowError("Name too long"))
                }
            }
            useCases.updateUserUseCase(updateUser = UpdateUser(username = name))
            _actions.send(Action.NavigateBack)
        }
    }
}