package ru.kheynov.secretsanta.domain.use_cases.game

data class GameUseCases(
    val joinGameUseCase: JoinGameUseCase,
    val leaveGameUseCase: LeaveGameUseCase,
    val kickUserUseCase: KickUserUseCase,
    val startGameUseCase: StartGameUseCase,
    val stopGameUseCase: StopGameUseCase,
    val getGameInfoUseCase: GetGameInfoUseCase,
)