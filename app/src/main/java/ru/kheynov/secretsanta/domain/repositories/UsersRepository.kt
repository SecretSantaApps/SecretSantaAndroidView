package ru.kheynov.secretsanta.domain.repositories

import ru.kheynov.secretsanta.domain.entities.RegisterUser
import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.entities.UpdateUser
import ru.kheynov.secretsanta.domain.entities.UserInfo
import ru.kheynov.secretsanta.utils.Resource

interface UsersRepository {
    suspend fun registerUser(user: RegisterUser): Resource<Unit>
    suspend fun deleteUser(): Resource<Unit>
    suspend fun updateUser(updateRequest: UpdateUser): Resource<Unit>
    suspend fun getSelfInfo(): Resource<UserInfo>
    suspend fun getRoomsList(): Resource<List<RoomDTO.RoomThumbnailInfo>>
    suspend fun checkUserRegistered(): Resource<Boolean>
}