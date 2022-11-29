package ru.kheynov.secretsanta.domain.use_cases

import ru.kheynov.secretsanta.domain.use_cases.users.CheckUserRegistered
import ru.kheynov.secretsanta.domain.use_cases.users.DeleteUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.GetFirebaseUserNameUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.GetSelfInfoUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.RegisterUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.UpdateUserUseCase

data class UseCases(
    val registerUserUseCase: RegisterUserUseCase,
    val deleteUserUseCase: DeleteUserUseCase,
    val updateUserUseCase: UpdateUserUseCase,
    val getSelfInfoUseCase: GetSelfInfoUseCase,
    val checkUserRegistered: CheckUserRegistered,
    val getFirebaseUserNameUseCase: GetFirebaseUserNameUseCase,
)