package ru.kheynov.secretsanta.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.kheynov.secretsanta.domain.entities.GameDTO.*

interface GameAPI {
    @POST("game/join")
    suspend fun joinRoom(@Body request: Join)

    @POST("game/leave")
    suspend fun leaveRoom(@Body request: Leave)

    @POST("game/kick")
    suspend fun kickUser(@Body request: KickUser)

    @POST("game/start")
    suspend fun startGame(@Body request: Start)

    @POST("game/stop")
    suspend fun stopGame(@Body request: Stop)

    @GET("game/info")
    suspend fun getGameInfo(@Query("roomName") roomName: String): RoomInfo
}