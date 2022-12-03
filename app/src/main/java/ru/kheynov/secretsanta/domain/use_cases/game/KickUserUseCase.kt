package ru.kheynov.secretsanta.domain.use_cases.game

import ru.kheynov.secretsanta.domain.entities.GameDTO
import ru.kheynov.secretsanta.domain.repositories.GameRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class KickUserUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(
        request: GameDTO.KickUser,
    ): Resource<Unit> {
        tokenRepository.fetchToken()
        return gameRepository.kickUser(request)
    }
}