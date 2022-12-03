package ru.kheynov.secretsanta.data.repositories

import retrofit2.HttpException
import ru.kheynov.secretsanta.data.api.RoomsAPI
import ru.kheynov.secretsanta.data.dto.RoomDTO.*
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
            Resource.Failure(when (e.code()) {
                400 -> UserNotExistsException()
                409 -> RoomAlreadyExistsException()
                else -> e
            })
        } catch (e: Exception) {

            Resource.Failure(e)
        }
    }

    override suspend fun deleteRoom(room: Delete): Resource<Unit> {
        return try {
            roomsAPI.deleteRoom(room)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Failure(when (e.code()) {
                400 -> {
                    if (e.message().contains("User")) UserNotExistsException()
                    else RoomNotExistsException()
                }
                else -> e
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun updateRoom(room: Update): Resource<Unit> {
        return try {
            roomsAPI.updateRoom(room)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Resource.Failure(when (e.code()) {
                400 -> {
                    if (e.message().contains("User")) UserNotExistsException()
                    else RoomNotExistsException()
                }
                403 -> ForbiddenException()
                else -> e
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun getRoomInfo(room: GetRoomInfo): Resource<Info> {
        return try {
            val res = roomsAPI.getRoomInfo(room)
            Resource.Success(res)
        } catch (e: HttpException) {
            Resource.Failure(when (e.code()) {
                400 -> {
                    if (e.message().contains("User")) UserNotExistsException()
                    else RoomNotExistsException()
                }
                403 -> ForbiddenException()
                else -> e
            })
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }
}