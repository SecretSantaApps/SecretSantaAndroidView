package ru.kheynov.secretsanta.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(val username: String?)

@Serializable
data class UpdateUser(val username: String)

@Serializable
data class UserInfo(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val username: String,
)