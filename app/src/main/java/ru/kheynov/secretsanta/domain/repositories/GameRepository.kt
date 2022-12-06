package ru.kheynov.secretsanta.domain.repositories

import ru.kheynov.secretsanta.domain.entities.GameDTO.KickUser
import ru.kheynov.secretsanta.domain.entities.GameDTO.RoomInfo
import ru.kheynov.secretsanta.utils.Resource

interface GameRepository {
    suspend fun joinRoom(roomId: String, password: String): Resource<Unit>
    suspend fun leaveRoom(roomId: String): Resource<Unit>
    suspend fun kickUser(request: KickUser): Resource<Unit>
    suspend fun startGame(roomId: String): Resource<Unit>
    suspend fun stopGame(roomId: String): Resource<Unit>
    suspend fun getGameInfo(roomId: String): Resource<RoomInfo>
}