package ru.kheynov.secretsanta.domain.repositories

import ru.kheynov.secretsanta.domain.entities.RoomDTO.*
import ru.kheynov.secretsanta.utils.Resource

interface RoomsRepository {
    suspend fun createRoom(room: Create): Resource<Info>
    suspend fun deleteRoom(room: Delete): Resource<Unit>
    suspend fun updateRoom(room: Update): Resource<Unit>
    suspend fun getRoomInfo(room: GetRoomInfo): Resource<Info>
}