package ru.kheynov.secretsanta.domain.use_cases.game

import ru.kheynov.secretsanta.domain.repositories.GameRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class StopGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(
        roomId: String,
    ): Resource<Unit> {
        return gameRepository.stopGame(roomId)
    }
}