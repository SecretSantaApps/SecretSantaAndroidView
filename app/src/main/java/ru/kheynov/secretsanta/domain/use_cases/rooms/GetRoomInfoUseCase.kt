package ru.kheynov.secretsanta.domain.use_cases.rooms

import ru.kheynov.secretsanta.data.dto.RoomDTO
import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class GetRoomInfoUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val roomsRepository: RoomsRepository,
) {
    suspend operator fun invoke(
        room: RoomDTO.GetRoomInfo,
    ): Resource<RoomDTO.Info> {
        tokenRepository.fetchToken()
        return roomsRepository.getRoomInfo(room)
    }
}