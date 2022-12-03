package ru.kheynov.secretsanta.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import ru.kheynov.secretsanta.domain.entities.RoomDTO.*


interface RoomsAPI {
    @POST("room")
    suspend fun createRoom(@Body room: Create): Info

    @DELETE("room")
    suspend fun deleteRoom(@Body room: Delete): Delete

    @PATCH
    suspend fun updateRoom(@Body room: Update)

    @GET("room")
    suspend fun getRoomInfo(@Body room: GetRoomInfo): Info
}