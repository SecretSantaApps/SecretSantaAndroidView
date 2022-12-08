package ru.kheynov.secretsanta.data.repositories

import retrofit2.HttpException
import ru.kheynov.secretsanta.data.api.RoomsAPI
import ru.kheynov.secretsanta.domain.entities.RoomDTO.*
import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.utils.ForbiddenException
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.RoomAlreadyExistsException
import ru.kheynov.secretsanta.utils.RoomNotExistsException
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomsRepositoryImpl @Inject constructor(
    private val roomsAPI: RoomsAPI,
) : RoomsRepository {
    override suspend fun createRoom(room: Create): Resource<Info> {
        return try {
            val res = roomsAPI.createRoom(room)
            Resource.Success(res)
        } catch (e: HttpException) {
            Resource.Failure(
                when (e.code()) {
                    400 -> UserNotExistsException()
                    409 -> RoomAlreadyExistsException()
                    else -> e
                }
            )
        } catch (e: Exception) {

            Resource.Failure(e)
        }
    }

    override suspend fun deleteRoom(roomId: String): Resource<Unit> {
        return try {
            roomsAPI.deleteRoom(roomId)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Failure(
                when (e.code()) {
                    400 -> {
                        if (e.response()?.errorBody()?.toString()
                                ?.contains("User") == true
                        ) UserNotExistsException()
                        else RoomNotExistsException()
                    }
                    else -> e
                }
            )
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun updateRoom(roomId: String, room: Update): Resource<Unit> {
        return try {
            roomsAPI.updateRoom(roomId, room)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Failure(
                when (e.code()) {
                    400 -> {
                        if (e.response()?.errorBody()?.toString()
                                ?.contains("User") == true
                        ) UserNotExistsException()
                        else RoomNotExistsException()
                    }
                    403 -> ForbiddenException()
                    else -> e
                }
            )
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun getRoomInfo(roomId: String): Resource<Info> {
        return try {
            val res = roomsAPI.getRoomInfo(roomId)
            Resource.Success(res)
        } catch (e: HttpException) {
            Resource.Failure(
                when (e.code()) {
                    400 -> {
                        if (e.response()?.errorBody()?.toString()
                                ?.contains("User") == true
                        ) UserNotExistsException()
                        else RoomNotExistsException()
                    }
                    403 -> ForbiddenException()
                    else -> e
                }
            )
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}