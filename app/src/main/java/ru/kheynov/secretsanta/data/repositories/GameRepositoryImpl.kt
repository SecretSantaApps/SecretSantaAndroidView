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
    override suspend fun joinRoom(request: GameDTO.Join): Resource<Unit> {
        return try {
            gameAPI.joinRoom(request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(
                when (e.code()) {
                    400 -> {
                        if (e.message().contains("User")) UserNotExistsException()
                        else RoomNotExistsException()
                    }
                    403 -> ForbiddenException()
                    409 -> {
                        if (e.message().contains("Game")) GameAlreadyStartedException()
                        else UserAlreadyInRoomException()
                    }
                    else -> e
                }
            )
            else Resource.Failure(e)
        }
    }

    override suspend fun leaveRoom(request: GameDTO.Leave): Resource<Unit> {
        return try {
            gameAPI.leaveRoom(request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(
                when (e.code()) {
                    400 -> {
                        if (e.message().contains("User")) UserNotExistsException()
                        else RoomNotExistsException()
                    }
                    403 -> UserNotInTheRoomException()
                    409 -> GameAlreadyStartedException()
                    else -> e
                }
            )
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
                    with(e.message()) {
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

    override suspend fun startGame(request: GameDTO.Start): Resource<Unit> {
        return try {
            gameAPI.startGame(request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(when (e.code()) {
                400 -> {
                    with(e.message()) {
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

    override suspend fun stopGame(request: GameDTO.Stop): Resource<Unit> {
        return try {
            gameAPI.stopGame(request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(when (e.code()) {
                400 -> {
                    with(e.message()) {
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

    override suspend fun getGameInfo(request: GameDTO.GetRoomInfo): Resource<GameDTO.RoomInfo> {
        return try {
            val res = gameAPI.getGameInfo(request.roomName)
            Resource.Success(res)
        } catch (e: Exception) {
            if (e is HttpException) Resource.Failure(when (e.code()) {
                400 -> {
                    with(e.message()) {
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