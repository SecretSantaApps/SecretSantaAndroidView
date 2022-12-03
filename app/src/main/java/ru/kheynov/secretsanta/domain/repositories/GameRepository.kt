package ru.kheynov.secretsanta.domain.repositories

import ru.kheynov.secretsanta.domain.entities.GameDTO.*
import ru.kheynov.secretsanta.utils.Resource

interface GameRepository {
    suspend fun joinRoom(request: Join): Resource<Unit>
    suspend fun leaveRoom(request: Leave): Resource<Unit>
    suspend fun kickUser(request: KickUser): Resource<Unit>
    suspend fun startGame(request: Start): Resource<Unit>
    suspend fun stopGame(request: Stop): Resource<Unit>
    suspend fun getGameInfo(request: GetRoomInfo): Resource<RoomInfo>
}