package ru.kheynov.secretsanta.presentation.screens.room_details

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
import retrofit2.HttpException
import ru.kheynov.secretsanta.R
import ru.kheynov.secretsanta.domain.entities.GameDTO
import ru.kheynov.secretsanta.domain.use_cases.game.GameUseCases
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.presentation.screens.room_details.RoomDetailsViewModel.Action.ShowError
import ru.kheynov.secretsanta.presentation.screens.room_details.RoomDetailsViewModel.State.Error
import ru.kheynov.secretsanta.presentation.screens.room_details.RoomDetailsViewModel.State.Loading
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.SantaException
import ru.kheynov.secretsanta.utils.UiText
import ru.kheynov.secretsanta.utils.UiText.PlainText
import ru.kheynov.secretsanta.utils.UiText.StringResource
import javax.inject.Inject

@HiltViewModel
class RoomDetailsViewModel @Inject constructor(
    private val gameUseCases: GameUseCases,
    private val userUseCases: UsersUseCases,
) : ViewModel() {
    sealed interface Action {
        data class ShowError(val error: UiText) : Action
        object NavigateBack : Action
    }
    
    sealed interface State {
        object Loading : State
        data class Loaded(val roomInfo: GameDTO.RoomInfo, val userId: String) : State
        data class Error(val error: Exception) : State
    }
    
    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    
    private val _state = MutableStateFlow<State>(Loading)
    val state: StateFlow<State> = _state
    
    private var roomId: String? = null
    
    fun setRoomId(roomId: String) {
        this.roomId = roomId
    }
    
    private val ioDispatcher = Dispatchers.IO
    private val mainDispatcher = Dispatchers.Main
    
    fun loadInfo() {
        viewModelScope.launch {
            _state.value = Loading
            if (roomId.isNullOrBlank()) {
                _actions.send(ShowError(StringResource(R.string.error)))
                return@launch
            }
            val info = withContext(ioDispatcher) {
                userUseCases.getSelfInfoUseCase()
            }
            if (info is Resource.Failure) {
                _actions.send(ShowError(StringResource(R.string.error)))
                return@launch
            }
            val userId = (info as Resource.Success).result.userId
            val res = withContext(ioDispatcher) {
                gameUseCases.getGameInfoUseCase(roomId!!)
            }
            when (res) {
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result, userId)
                }
                is Resource.Failure -> {
                    if (res.exception is SantaException || res.exception is HttpException) {
                        _state.value = Error(res.exception)
                    } else {
                        _actions.send(ShowError(PlainText(res.exception.message.toString())))
                    }
                }
            }
        }
    }
    
    fun startGame() {
        viewModelScope.launch {
            if (roomId.isNullOrBlank()) {
                _actions.send(ShowError(StringResource(R.string.error)))
                return@launch
            }
            val lastState = _state.value
            _state.value = Loading
            val res = withContext(ioDispatcher) {
                gameUseCases.startGameUseCase(roomId!!)
            }
            when (res) {
                is Resource.Failure -> {
                    withContext(mainDispatcher) {
                        _actions.send(ShowError(PlainText(res.exception.javaClass.simpleName.toString())))
                        _state.value = lastState
                    }
                }
                is Resource.Success -> loadInfo()
            }
        }
        
    }
    
    fun stopGame() {
        viewModelScope.launch {
            if (roomId.isNullOrBlank()) {
                _actions.send(ShowError(StringResource(R.string.error)))
                return@launch
            }
            val lastState = _state.value
            val res = withContext(ioDispatcher) {
                gameUseCases.stopGameUseCase(roomId!!)
            }
            when (res) {
                is Resource.Failure -> {
                    _actions.send(ShowError(PlainText(res.exception.javaClass.simpleName.toString())))
                    _state.value = lastState
                }
                is Resource.Success -> loadInfo()
            }
        }
    }
    
    fun kickUser(user: String) {
        viewModelScope.launch {
            val roomId = (_state.value as State.Loaded).roomInfo.id
            val lastState = _state.value
            withContext(mainDispatcher) {
                _state.value = Loading
            }
            val res = withContext(ioDispatcher) {
                gameUseCases.kickUserUseCase(GameDTO.KickUser(user, roomId))
            }
            when (res) {
                is Resource.Success -> {
                    loadInfo()
                }
                is Resource.Failure -> {
                    withContext(mainDispatcher) {
                        _actions.send(ShowError(PlainText(res.exception.toString())))
                        _state.value = lastState
                    }
                }
            }
        }
    }
    
    fun leaveRoom() {
        viewModelScope.launch {
            val roomId = (_state.value as State.Loaded).roomInfo.id
            val lastState = _state.value
            _state.value = Loading
            
            val res = withContext(ioDispatcher) {
                gameUseCases.leaveGameUseCase(roomId)
            }
            
            when (res) {
                is Resource.Success -> {
                    _actions.send(Action.NavigateBack)
                }
                is Resource.Failure -> {
                    _actions.send(ShowError(PlainText(res.exception.toString())))
                    _state.value = lastState
                }
            }
        }
    }
    
}