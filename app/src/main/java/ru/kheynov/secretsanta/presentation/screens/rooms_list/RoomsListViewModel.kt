package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.kheynov.secretsanta.domain.use_cases.UseCases
import ru.kheynov.secretsanta.utils.Resource
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

    fun loadData() {
        viewModelScope.launch {
            //TODO: test data, change in production
            _state.value = State.Loading
            when (val res = useCases.getSelfInfoUseCase()) {
                is Resource.Failure -> {
                    Log.e(TAG, "Something went wrong", res.exception)
                    _state.value = State.Loaded("")
                }
                is Resource.Success -> {
                    _state.value = State.Loaded(res.result.username)
                }
            }
        }
    }
}