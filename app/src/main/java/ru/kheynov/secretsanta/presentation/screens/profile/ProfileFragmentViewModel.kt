package ru.kheynov.secretsanta.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.presentation.screens.profile.ProfileFragmentViewModel.Action.*
import ru.kheynov.secretsanta.presentation.screens.profile.ProfileFragmentViewModel.State.*
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.UiText
import ru.kheynov.secretsanta.utils.UiText.StringResource
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val useCases: UsersUseCases,
) : ViewModel() {
    sealed interface State {
        object Loading : State
        data class Loaded(val name: String) : State
        data class Error(val error: Exception) : State
    }
    
    sealed interface Action {
        object NavigateToLoginScreen : Action
        object NavigateToEditUser : Action
        data class ShowError(val error: UiText) : Action
    }
    
    private val _state = MutableStateFlow<State>(Loading)
    val state: StateFlow<State> = _state
    
    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    
    private lateinit var username: String
    
    init {
        updateUsername()
    }
    
    private val ioDispatcher = Dispatchers.IO
    
    fun updateUsername() {
        viewModelScope.launch {
            _state.value = Loading
            val res = withContext(ioDispatcher) {
                useCases.getSelfInfoUseCase()
            }
            when (res) {
                is Resource.Failure -> {
                    _state.value = Error(res.exception)
                }
                is Resource.Success -> {
                    username = res.result.username
                    _state.value = Loaded(username)
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _actions.send(NavigateToLoginScreen)
        }
    }
    
    fun deleteAccount() {
        viewModelScope.launch {
            val res = withContext(ioDispatcher) {
                useCases.deleteUserUseCase()
            }
            when (res) {
                is Resource.Failure -> {
                    if (res.exception is UserNotExistsException) {
                        _actions.send(ShowError(StringResource(R.string.user_not_exists_error)))
                    }
                }
                is Resource.Success -> _actions.send(NavigateToLoginScreen)
            }
        }
    }
    
    fun editUsername() {
        viewModelScope.launch {
            _actions.send(NavigateToEditUser)
        }
    }
}