package ru.kheynov.secretsanta.presentation.screens.room_details

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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateRoomFragmentViewModel @Inject constructor(
    private val useCases: RoomsUseCases,
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    sealed interface State {
        object Loading : State
        data class Loaded(
            val name: String? = null,
            val deadline: LocalDate? = null,
            val maxPrice: Int? = null,
            val gameStarted: Boolean = false,
            val membersCount: Int,
        ) : State
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    sealed interface Action {
        data class ShowError(val error: String) : Action
//        object RouteToLogin : Action
    }

    fun createRoom(room: RoomDTO.Create) {
        viewModelScope.launch {
            useCases
        }
    }

}