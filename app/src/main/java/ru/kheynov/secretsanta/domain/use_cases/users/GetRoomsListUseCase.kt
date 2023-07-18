package ru.kheynov.secretsanta.domain.use_cases.users

import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

class GetRoomsListUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
) {
    suspend operator fun invoke(): Resource<List<RoomDTO.RoomThumbnailInfo>> {
        val userCheck = usersRepository.checkUserRegistered()
        if (userCheck is Resource.Success && !userCheck.result)
            return Resource.Failure(UserNotExistsException())
        return usersRepository.getRoomsList()
    }
}