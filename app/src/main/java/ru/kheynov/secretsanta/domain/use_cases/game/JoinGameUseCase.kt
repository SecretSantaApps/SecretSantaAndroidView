package ru.kheynov.secretsanta.domain.use_cases.game

import ru.kheynov.secretsanta.domain.repositories.GameRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class JoinGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(
        roomId: String,
        password: String
    ): Resource<Unit> {
        return gameRepository.joinRoom(roomId, password)
    }
}