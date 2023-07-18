package ru.kheynov.secretsanta.domain.use_cases.rooms

import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class GetRoomInfoUseCase @Inject constructor(
    private val roomsRepository: RoomsRepository,
) {
    suspend operator fun invoke(
        roomId: String,
    ): Resource<RoomDTO.Info> {
        return roomsRepository.getRoomInfo(roomId)
    }
}