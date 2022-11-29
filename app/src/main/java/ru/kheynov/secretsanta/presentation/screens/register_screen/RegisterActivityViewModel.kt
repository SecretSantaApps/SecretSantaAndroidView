package ru.kheynov.secretsanta.presentation.screens.register_screen

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
import ru.kheynov.secretsanta.data.dto.RegisterUser
import ru.kheynov.secretsanta.domain.use_cases.UseCases
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject

private const val TAG = "RegisterActivityViewModel"

@HiltViewModel
class RegisterActivityViewModel @Inject constructor(
    private val useCases: UseCases,
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    sealed interface State {
        data class Idle(val username: String) : State
        object Loading : State
    }

    init {
        getUsername()
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    sealed interface Action {
        data class ShowError(val error: String) : Action
        object RouteToMain : Action
    }

    fun registerUser(user: RegisterUser) {
        viewModelScope.launch {
            _state.value = State.Loading
            when (val res = useCases.registerUserUseCase(user)) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.message.toString()))
                    Log.e(TAG, "Something went wrong", res.exception)
                }
                is Resource.Success -> {
                    _actions.send(Action.RouteToMain)
                }
            }
        }
    }

    private fun getUsername() {
        viewModelScope.launch {
            _state.value = State.Loading
            when (val res = useCases.getFirebaseUserNameUseCase()) {
                is Resource.Failure -> {
                    _actions.send(Action.ShowError(res.exception.message.toString()))
                    Log.e(TAG, "Something went wrong", res.exception)
                    _state.value = State.Idle("")
                }
                is Resource.Success -> {
                    _state.value = State.Idle(res.result)
                }
            }
        }
    }


}