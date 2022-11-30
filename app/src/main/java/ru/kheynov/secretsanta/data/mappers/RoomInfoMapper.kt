package ru.kheynov.secretsanta.data.mappers

import ru.kheynov.secretsanta.data.dto.RoomInfoDTO
import ru.kheynov.secretsanta.domain.entities.RoomInfo


fun RoomInfoDTO.toRoomInfo(): RoomInfo {
    return RoomInfo(
        name = this.name,
        deadline = this.date,
        ownerId = this.ownerId,
        maxPrice = this.maxPrice,
        gameStarted = this.gameStarted,
        membersCount = this.membersCount
    )
}