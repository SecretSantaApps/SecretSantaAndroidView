package ru.kheynov.secretsanta.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    sealed interface Room {
        object RoomsList : Room
        object CreateRoom : Room
        object Profile : Room
    }

    sealed interface Action {
        object NavigateToRoomsList : Action
        object NavigateToCreateRoom : Action
        object NavigateToProfile : Action
    }

    private val _room = MutableStateFlow<Room>(Room.RoomsList)
    val room: StateFlow<Room> = _room

    private val _action: Channel<Action> = Channel(Channel.BUFFERED)
    val action: Flow<Action> = _action.receiveAsFlow()

    init {
        navigateToRoomsList()
    }

    fun navigateToRoomsList() = viewModelScope.launch {
        _room.value = Room.RoomsList
        _action.send(Action.NavigateToRoomsList)
    }

    fun navigateToProfile() = viewModelScope.launch {
        _room.value = Room.Profile
        _action.send(Action.NavigateToProfile)
    }

    fun navigateToCreateRoom() = viewModelScope.launch {
        _room.value = Room.CreateRoom
        _action.send(Action.NavigateToCreateRoom)
    }

}