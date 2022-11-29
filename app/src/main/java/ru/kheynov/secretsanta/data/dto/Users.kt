package ru.kheynov.secretsanta.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RegisterUser(val username: String?)

@kotlinx.serialization.Serializable
data class UpdateUser(val username: String)

@kotlinx.serialization.Serializable
data class UserInfo(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val username: String,
)