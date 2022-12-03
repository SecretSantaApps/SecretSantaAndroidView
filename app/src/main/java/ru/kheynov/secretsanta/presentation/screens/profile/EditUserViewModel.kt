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
    }

    sealed interface Action {
        object NavigateBack : Action
        object ShowError : Action
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    init {
        viewModelScope.launch {
            when (val res = useCases.getSelfInfoUseCase()) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError)
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
            //TODO: add name validation
            useCases.updateUserUseCase(UpdateUser(name))
            _actions.send(Action.NavigateBack)
        }
    }
}