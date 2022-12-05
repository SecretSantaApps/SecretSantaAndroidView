package ru.kheynov.secretsanta.domain.entities

data class RoomItem(
    val roomName: String,
    val membersCount: String,
    val gameState: String,
    val date: String,
    var gameStateColor: Int? = null,
)