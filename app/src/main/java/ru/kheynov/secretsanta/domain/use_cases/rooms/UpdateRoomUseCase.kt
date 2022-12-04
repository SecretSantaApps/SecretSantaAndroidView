package ru.kheynov.secretsanta.domain.use_cases.rooms

import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class UpdateRoomUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val roomsRepository: RoomsRepository,
) {
    suspend operator fun invoke(
        room: RoomDTO.Update,
    ): Resource<Unit> {
        tokenRepository.fetchToken()
        return roomsRepository.updateRoom(room)
    }
}