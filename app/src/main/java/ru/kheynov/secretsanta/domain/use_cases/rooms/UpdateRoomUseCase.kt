package ru.kheynov.secretsanta.domain.use_cases.rooms

import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class UpdateRoomUseCase @Inject constructor(
    private val roomsRepository: RoomsRepository,
) {
    suspend operator fun invoke(
        roomId: String,
        room: RoomDTO.Update,
    ): Resource<Unit> {
        return roomsRepository.updateRoom(roomId, room)
    }
}