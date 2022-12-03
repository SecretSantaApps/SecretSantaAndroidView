package ru.kheynov.secretsanta.domain.use_cases.game

import ru.kheynov.secretsanta.domain.entities.GameDTO
import ru.kheynov.secretsanta.domain.repositories.GameRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class StopGameUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(
        request: GameDTO.Stop,
    ): Resource<Unit> {
        tokenRepository.fetchToken()
        return gameRepository.stopGame(request)
    }
}