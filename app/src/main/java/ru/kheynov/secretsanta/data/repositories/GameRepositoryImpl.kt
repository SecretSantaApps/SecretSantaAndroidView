package ru.kheynov.secretsanta.data.repositories

import retrofit2.HttpException
import ru.kheynov.secretsanta.data.api.GameAPI
import ru.kheynov.secretsanta.domain.entities.GameDTO
import ru.kheynov.secretsanta.domain.repositories.GameRepository
import ru.kheynov.secretsanta.utils.ForbiddenException
import ru.kheynov.secretsanta.utils.GameAlreadyStartedException
import ru.kheynov.secretsanta.utils.GameAlreadyStoppedException
import ru.kheynov.secretsanta.utils.NotEnoughPlayersException
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.RoomNotExistsException
import ru.kheynov.secretsanta.utils.UserAlreadyInRoomException
import ru.kheynov.secretsanta.utils.UserNotExistsException
import ru.kheynov.secretsanta.utils.UserNotInTheRoomException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val gameAPI: GameAPI
) : GameRepository {
    override suspend fun joinRoom(roomId: String, password: String): Resource<Unit> {
        return try {
            gameAPI.joinRoom(roomId = roomId, password = password)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(e.response()?.errorBody()?.string().toString()
                .let { message ->
                    when (e.code()) {
                        400 -> {
                            if (message.contains("User")) UserNotExistsException()
                            else RoomNotExistsException()
                        }
                        403 -> ForbiddenException()
                        409 -> {
                            if (message.contains("Game")) GameAlreadyStartedException()
                            else UserAlreadyInRoomException()
                        }
                        else -> e
                    }
                })
            else Resource.Failure(e)
        }
    }

    override suspend fun leaveRoom(roomId: String): Resource<Unit> {
        return try {
            gameAPI.leaveRoom(roomId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(e.response()?.errorBody()?.string().toString()
                .let { message ->
                    when (e.code()) {
                        400 -> {
                            if (message.contains("User")) UserNotExistsException()
                            else RoomNotExistsException()
                        }
                        403 -> UserNotInTheRoomException()
                        409 -> GameAlreadyStartedException()
                        else -> e
                    }
                })
            else Resource.Failure(e)
        }
    }

    override suspend fun kickUser(request: GameDTO.KickUser): Resource<Unit> {
        return try {
            gameAPI.kickUser(request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(when (e.code()) {
                400 -> {
                    with(e.response()?.errorBody()?.string().toString()) {
                        when {
                            contains("User") -> if (contains("exists")) UserNotExistsException() else UserNotInTheRoomException()
                            contains("Room") -> RoomNotExistsException()
                            else -> e
                        }
                    }
                }
                403 -> ForbiddenException()
                409 -> GameAlreadyStartedException()
                else -> e
            })
            else Resource.Failure(e)
        }
    }

    override suspend fun startGame(roomId: String): Resource<Unit> {
        return try {
            gameAPI.startGame(roomId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(when (e.code()) {
                400 -> {
                    with(e.response()?.errorBody()?.string().toString()) {
                        when {
                            contains("User") -> UserNotExistsException()
                            contains("Room") -> RoomNotExistsException()
                            else -> NotEnoughPlayersException()
                        }
                    }
                }
                403 -> ForbiddenException()
                409 -> GameAlreadyStartedException()
                else -> e
            })
            else Resource.Failure(e)
        }
    }

    override suspend fun stopGame(roomId: String): Resource<Unit> {
        return try {
            gameAPI.stopGame(roomId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(when (e.code()) {
                400 -> {
                    with(e.response()?.errorBody()?.string().toString()) {
                        when {
                            contains("User") -> UserNotExistsException()
                            contains("Room") -> RoomNotExistsException()
                            else -> e
                        }
                    }
                }
                403 -> ForbiddenException()
                409 -> GameAlreadyStoppedException()
                else -> e
            })
            else Resource.Failure(e)
        }
    }

    override suspend fun getGameInfo(roomId: String): Resource<GameDTO.RoomInfo> {
        return try {
            val res = gameAPI.getGameInfo(roomId)
            Resource.Success(res)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(when (e.code()) {
                400 -> {
                    with(e.response()?.errorBody()?.string().toString()) {
                        when {
                            contains("User") -> UserNotExistsException()
                            contains("Room") -> RoomNotExistsException()
                            else -> e
                        }
                    }
                }
                403 -> ForbiddenException()
                else -> e
            })
            else Resource.Failure(e)
        }
    }
}