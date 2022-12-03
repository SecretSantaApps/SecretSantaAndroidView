package ru.kheynov.secretsanta.domain.entities

import java.time.LocalDate

data class Room(
    val name: String,
    val date: LocalDate? = null,
    val password: String? = null,
    val ownerId: String? = null,
    val maxPrice: Int? = null,
    val gameStarted: Boolean? = null,
    val membersCount: Int? = null,
)