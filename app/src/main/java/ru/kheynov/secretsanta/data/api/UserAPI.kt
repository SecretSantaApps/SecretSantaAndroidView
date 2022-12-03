package ru.kheynov.secretsanta.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import ru.kheynov.secretsanta.domain.entities.RegisterUser
import ru.kheynov.secretsanta.domain.entities.RoomDTO
import ru.kheynov.secretsanta.domain.entities.UpdateUser
import ru.kheynov.secretsanta.domain.entities.UserInfo

interface UserAPI {
    @POST("user")
    suspend fun registerUser(@Body body: RegisterUser)

    @DELETE("user")
    suspend fun deleteUser()

    @PATCH("user")
    suspend fun updateUser(@Body body: UpdateUser)

    @GET("user")
    suspend fun getSelfInfo(): UserInfo

    @GET("user")
    suspend fun checkUserRegistered()


    @GET("user/rooms")
    fun getRoomsList(): List<RoomDTO.RoomThumbnailInfo>
}