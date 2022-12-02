package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.domain.use_cases.UseCases
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

private const val TAG = "RoomsListViewModel"

@HiltViewModel
class RoomsListViewModel @Inject constructor(
    private val useCases: UseCases,
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    sealed interface State {
        object Loading : State
        data class Loaded(val username: String) : State
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    sealed interface Action {
        data class ShowError(val error: String) : Action
        object RouteToLogin : Action
    }

    fun loadData() {
        viewModelScope.launch {
            //TODO: test data, change in production
            _state.value = State.Loading
            when (val res = useCases.getSelfInfoUseCase()) {
                is Resource.Failure -> {
                    when (res.exception) {
                        is UserNotExistsException -> {
                            Log.e(TAG, "User not registered")
                            _actions.send(Action.RouteToLogin)
                        }
                        is CancellationException -> {
                            Log.i(TAG, "Job cancelled")
                        }
                        else -> {
                            Log.e(TAG, "Something went wrong", res.exception)
                            _actions.send(Action.ShowError(res.exception.message.toString()))
                        }
                    }
                }
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result.username)
                }
            }
        }
    }
}