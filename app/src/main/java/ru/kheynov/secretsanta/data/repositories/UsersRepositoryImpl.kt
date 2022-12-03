package ru.kheynov.secretsanta.data.repositories

import retrofit2.HttpException
import ru.kheynov.secretsanta.data.api.UserAPI
import ru.kheynov.secretsanta.domain.entities.RegisterUser
import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.entities.UpdateUser
import ru.kheynov.secretsanta.domain.entities.UserInfo
import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.UserAlreadyExistsException
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(private val userAPI: UserAPI) : UsersRepository {
    override suspend fun registerUser(user: RegisterUser): Resource<Unit> {
        return try {
            userAPI.registerUser(user)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Failure(when (e.code()) {
                409 -> UserAlreadyExistsException()
                else -> e
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun deleteUser(): Resource<Unit> {
        return try {
            userAPI.deleteUser()
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Failure(when (e.code()) {
                400 -> UserNotExistsException()
                else -> e
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun updateUser(updateRequest: UpdateUser): Resource<Unit> {
        return try {
            userAPI.updateUser(updateRequest)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Failure(when (e.code()) {
                409 -> UserNotExistsException()
                else -> e
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun getSelfInfo(): Resource<UserInfo> {
        return try {
            val userInfo = userAPI.getSelfInfo()
            Resource.Success(userInfo)
        } catch (e: HttpException) {
            Resource.Failure(when (e.code()) {
                400 -> UserNotExistsException()
                else -> e
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun checkUserRegistered(): Resource<Boolean> {
        return try {
            userAPI.checkUserRegistered()
            Resource.Success(true) // user registered
        } catch (e: HttpException) {
            if (e.code() == 400) Resource.Success(false) // user not registered
            else Resource.Failure(e) // another error
        } catch (e: Exception) {
            Resource.Failure(e) // another error
        }
    }

    override suspend fun getRoomsList(): Resource<List<RoomDTO.RoomThumbnailInfo>> {
        return try {
            val res = userAPI.getRoomsList()
            Resource.Success(res)
        } catch (e: HttpException) {
            Resource.Failure(if (e.code() == 400) UserNotExistsException() else e)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}