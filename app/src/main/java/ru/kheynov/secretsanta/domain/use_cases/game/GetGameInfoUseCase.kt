package ru.kheynov.secretsanta.domain.use_cases.game

import ru.kheynov.secretsanta.domain.entities.GameDTO
import ru.kheynov.secretsanta.domain.repositories.GameRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class GetGameInfoUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val gameRepository: GameRepository,
) {
    suspend operator fun invoke(
        request: GameDTO.GetRoomInfo,
    ): Resource<GameDTO.RoomInfo> {
        tokenRepository.fetchToken()
        return gameRepository.getGameInfo(request)
    }
}