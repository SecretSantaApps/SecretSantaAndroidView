package ru.kheynov.secretsanta.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kheynov.secretsanta.utils.LocalDateSerializer
import java.time.LocalDate

sealed interface GameDTO {
    @Serializable
    data class KickUser(
        @SerialName("user_id") val userId: String,
        @SerialName("room_id") val roomId: String,
    ) : GameDTO

    @Serializable
    data class RoomInfo(
        @SerialName("room_id") val id: String,
        @SerialName("room_name") val roomName: String,
        @SerialName("owner_id") val ownerId: String,
        val password: String?,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
        @SerialName("max_price") val max_price: Int?,
        val users: List<UserInfo>,
        val recipient: String?,
    ) : GameDTO
}