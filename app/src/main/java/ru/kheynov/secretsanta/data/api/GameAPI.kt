package ru.kheynov.secretsanta.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.kheynov.secretsanta.domain.entities.GameDTO.*

interface GameAPI {
    @POST("game/join")
    suspend fun joinRoom(@Query("id") roomId: String, @Query("pass") password: String)

    @POST("game/leave")
    suspend fun leaveRoom(@Query("id") roomId: String)

    @POST("game/kick")
    suspend fun kickUser(@Body request: KickUser)

    @POST("game/start")
    suspend fun startGame(@Query("id") roomId: String)

    @POST("game/stop")
    suspend fun stopGame(@Query("id") roomId: String)

    @GET("game/info")
    suspend fun getGameInfo(@Query("id") roomId: String): RoomInfo
}