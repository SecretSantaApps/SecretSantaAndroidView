package ru.kheynov.secretsanta.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import ru.kheynov.secretsanta.data.dto.RoomDTO.*


interface RoomsAPI {
    @POST("room")
    fun createRoom(@Body room: Create): Info

    @DELETE("room")
    fun deleteRoom(@Body room: Delete): Delete

    @PATCH
    fun updateRoom(@Body room: Update)

    @GET("room")
    fun getRoomInfo(@Body room: GetRoomInfo): Info
}