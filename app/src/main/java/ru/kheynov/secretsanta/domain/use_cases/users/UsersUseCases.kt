package ru.kheynov.secretsanta.domain.use_cases.users

data class UsersUseCases(
    val registerUserUseCase: RegisterUserUseCase,
    val deleteUserUseCase: DeleteUserUseCase,
    val updateUserUseCase: UpdateUserUseCase,
    val getSelfInfoUseCase: GetSelfInfoUseCase,
    val checkUserRegistered: CheckUserRegisteredUseCase,
    val getRoomsListUseCase: GetRoomsListUseCase,
)