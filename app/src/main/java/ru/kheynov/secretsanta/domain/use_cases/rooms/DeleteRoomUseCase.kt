package ru.kheynov.secretsanta.domain.use_cases.rooms

import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class DeleteRoomUseCase @Inject constructor(
    private val roomsRepository: RoomsRepository,
) {
    suspend operator fun invoke(
        roomId: String,
    ): Resource<Unit> {
        return roomsRepository.deleteRoom(roomId)
    }
}