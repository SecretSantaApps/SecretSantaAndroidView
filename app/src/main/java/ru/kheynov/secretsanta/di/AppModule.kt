package ru.kheynov.secretsanta.di

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.kheynov.secretsanta.data.api.UserAPI
import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.domain.use_cases.UseCases
import ru.kheynov.secretsanta.domain.use_cases.users.CheckUserRegisteredUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.DeleteUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.GetSelfInfoUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.RegisterUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.UpdateUserUseCase
import ru.kheynov.secretsanta.utils.AuthInterceptor
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://santa.s.kheynov.ru/api/v1/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    fun provideFirebaseInstance() = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthUIInstance() = AuthUI.getInstance()

    @Provides
    @Singleton
    fun provideTokenRepository(firebaseAuth: FirebaseAuth) = TokenRepository(firebaseAuth)

    @Provides
    fun provideOkHttpClient(
        tokenRepository: TokenRepository,
    ): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(AuthInterceptor(tokenRepository)).build()

    @Provides
    fun provideUseCases(
        tokenRepository: TokenRepository,
        usersRepository: UsersRepository,
    ) = UseCases(
        registerUserUseCase = RegisterUserUseCase(tokenRepository, usersRepository),
        deleteUserUseCase = DeleteUserUseCase(tokenRepository, usersRepository),
        updateUserUseCase = UpdateUserUseCase(tokenRepository, usersRepository),
        getSelfInfoUseCase = GetSelfInfoUseCase(tokenRepository, usersRepository),
        checkUserRegistered = CheckUserRegisteredUseCase(tokenRepository, usersRepository),
    )

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder().addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BASE_URL).client(httpClient).build()
    }

    @Provides
    @Singleton
    fun provideUsersApi(retrofit: Retrofit): UserAPI = retrofit.create(UserAPI::class.java)

}