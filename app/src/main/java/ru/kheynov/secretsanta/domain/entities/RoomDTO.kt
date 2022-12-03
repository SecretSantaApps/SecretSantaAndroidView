package ru.kheynov.secretsanta.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kheynov.utils.LocalDateSerializer
import java.time.LocalDate

sealed interface RoomDTO {

    @Serializable
    data class Info(
        @SerialName("room_name") val name: String,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
        val password: String,
        @SerialName("owner_id") val ownerId: String,
        @SerialName("max_price") val maxPrice: Int?,
        @SerialName("game_started") val gameStarted: Boolean,
        @SerialName("members_count") val membersCount: Int,
    ) : RoomDTO

    @Serializable
    data class Create(
        @SerialName("room_name") val roomName: String,
        val password: String?,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
        @SerialName("max_price") val maxPrice: Int?,
    ) : RoomDTO

    @Serializable
    data class Delete(
        @SerialName("room_name") val roomName: String,
    ) : RoomDTO

    @Serializable
    data class Update(
        @SerialName("room_name") val roomName: String,
        val password: String?,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
    )

    @Serializable
    data class GetRoomInfo(
        @SerialName("room_name") val roomName: String,
    )

    @Serializable
    data class RoomThumbnailInfo(
        @SerialName("room_name") val name: String,
        @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
        @SerialName("owner_id") val ownerId: String,
        @SerialName("max_price") val maxPrice: Int? = null,
        @SerialName("game_started") val gameStarted: Boolean = false,
        @SerialName("members_count") val membersCount: Int,
    ) : RoomDTO
}

