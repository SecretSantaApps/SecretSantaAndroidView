package ru.kheynov.secretsanta.presentation.screens.rooms_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.kheynov.secretsanta.data.dto.RegisterUser
import ru.kheynov.secretsanta.domain.use_cases.UseCases
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject

private const val TAG = "RoomsListViewModel"

@HiltViewModel
class RoomsListViewModel @Inject constructor(
    private val useCases: UseCases,
) : ViewModel() {

    fun loadData() {
        val user = RegisterUser("abobus")
        viewModelScope.launch {
            Log.i(TAG, "register user")
            when (val res = useCases.registerUserUseCase(user)) {
                is Resource.Failure -> {
                    Log.i(TAG, "Error body: ${(res.exception as HttpException).response()}")
                    when (res.exception.code()) {
                        409 -> Log.i(TAG, "User already exists")
                    }
                    Log.e(TAG, "Something went wrong", res.exception)
                }
                is Resource.Success -> Log.i(TAG, "Everything was ok")
            }
        }
    }
}