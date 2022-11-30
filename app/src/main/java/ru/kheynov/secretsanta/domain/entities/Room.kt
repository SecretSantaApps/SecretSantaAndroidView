package ru.kheynov.secretsanta.domain.entities

import java.time.LocalDate

data class RoomInfo(
    val name: String,
    val deadline: LocalDate?,
    val ownerId: String,
    val maxPrice: Int? = null,
    val gameStarted: Boolean = false,
    val membersCount: Int,
)