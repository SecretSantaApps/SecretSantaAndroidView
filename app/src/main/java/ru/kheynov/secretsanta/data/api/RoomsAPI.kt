package ru.kheynov.secretsanta.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import ru.kheynov.secretsanta.domain.entities.RoomDTO.*


interface RoomsAPI {
    @POST("room")
    suspend fun createRoom(@Body room: Create): Info

    @DELETE("room")
    suspend fun deleteRoom(@Query("id") roomId: String)

    @PATCH
    suspend fun updateRoom(@Query("id") roomId: String, update: Update)

    @GET("room")
    suspend fun getRoomInfo(@Query("id") roomId: String): Info
}