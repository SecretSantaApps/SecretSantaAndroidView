package ru.kheynov.secretsanta.domain.use_cases.rooms

import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(
    private val roomsRepository: RoomsRepository,
) {
    suspend operator fun invoke(
        room: RoomDTO.Create,
    ): Resource<RoomDTO.Info> {
        return roomsRepository.createRoom(room)
    }
}