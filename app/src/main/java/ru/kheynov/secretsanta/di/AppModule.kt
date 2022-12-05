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
import ru.kheynov.secretsanta.data.api.GameAPI
import ru.kheynov.secretsanta.data.api.RoomsAPI
import ru.kheynov.secretsanta.data.api.UserAPI
import ru.kheynov.secretsanta.data.repositories.GameRepositoryImpl
import ru.kheynov.secretsanta.data.repositories.RoomsRepositoryImpl
import ru.kheynov.secretsanta.data.repositories.UsersRepositoryImpl
import ru.kheynov.secretsanta.domain.repositories.GameRepository
import ru.kheynov.secretsanta.domain.repositories.RoomsRepository
import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.domain.use_cases.game.GameUseCases
import ru.kheynov.secretsanta.domain.use_cases.game.GetGameInfoUseCase
import ru.kheynov.secretsanta.domain.use_cases.game.JoinGameUseCase
import ru.kheynov.secretsanta.domain.use_cases.game.KickUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.game.LeaveGameUseCase
import ru.kheynov.secretsanta.domain.use_cases.game.StartGameUseCase
import ru.kheynov.secretsanta.domain.use_cases.game.StopGameUseCase
import ru.kheynov.secretsanta.domain.use_cases.rooms.CreateRoomUseCase
import ru.kheynov.secretsanta.domain.use_cases.rooms.DeleteRoomUseCase
import ru.kheynov.secretsanta.domain.use_cases.rooms.GetRoomInfoUseCase
import ru.kheynov.secretsanta.domain.use_cases.rooms.RoomsUseCases
import ru.kheynov.secretsanta.domain.use_cases.rooms.UpdateRoomUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.CheckUserRegisteredUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.DeleteUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.GetRoomsListUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.GetSelfInfoUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.RegisterUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.UpdateUserUseCase
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
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
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder().addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BASE_URL).client(httpClient).build()
    }

    @Provides
    @Singleton
    fun provideUsersApi(retrofit: Retrofit): UserAPI = retrofit.create(UserAPI::class.java)

    @Provides
    @Singleton
    fun provideRoomsApi(retrofit: Retrofit): RoomsAPI = retrofit.create(RoomsAPI::class.java)

    @Provides
    @Singleton
    fun provideGameApi(retrofit: Retrofit): GameAPI = retrofit.create(GameAPI::class.java)

    @Provides
    @Singleton
    fun provideUsersRepository(userAPI: UserAPI): UsersRepository = UsersRepositoryImpl(userAPI)

    @Provides
    @Singleton
    fun provideRoomsRepository(roomsAPI: RoomsAPI): RoomsRepository = RoomsRepositoryImpl(roomsAPI)

    @Provides
    @Singleton
    fun provideGameRepository(gameAPI: GameAPI): GameRepository = GameRepositoryImpl(gameAPI)

    @Provides
    fun provideUserUseCases(
        tokenRepository: TokenRepository,
        usersRepository: UsersRepository,
    ) = UsersUseCases(
        registerUserUseCase = RegisterUserUseCase(tokenRepository, usersRepository),
        deleteUserUseCase = DeleteUserUseCase(tokenRepository, usersRepository),
        updateUserUseCase = UpdateUserUseCase(tokenRepository, usersRepository),
        getSelfInfoUseCase = GetSelfInfoUseCase(tokenRepository, usersRepository),
        checkUserRegistered = CheckUserRegisteredUseCase(tokenRepository, usersRepository),
        getRoomsListUseCase = GetRoomsListUseCase(tokenRepository, usersRepository),
    )

    @Provides
    fun provideRoomsUseCases(
        tokenRepository: TokenRepository,
        roomsRepository: RoomsRepository,
    ) = RoomsUseCases(
        createRoomUseCase = CreateRoomUseCase(tokenRepository, roomsRepository),
        deleteRoomUseCase = DeleteRoomUseCase(tokenRepository, roomsRepository),
        getRoomInfoUseCase = GetRoomInfoUseCase(tokenRepository, roomsRepository),
        updateRoomUseCase = UpdateRoomUseCase(tokenRepository, roomsRepository),
    )

    @Provides
    fun provideGameUseCases(
        tokenRepository: TokenRepository,
        gameRepository: GameRepository,
    ) = GameUseCases(
        joinGameUseCase = JoinGameUseCase(tokenRepository, gameRepository),
        leaveGameUseCase = LeaveGameUseCase(tokenRepository, gameRepository),
        kickUserUseCase = KickUserUseCase(tokenRepository, gameRepository),
        startGameUseCase = StartGameUseCase(tokenRepository, gameRepository),
        stopGameUseCase = StopGameUseCase(tokenRepository, gameRepository),
        getGameInfoUseCase = GetGameInfoUseCase(tokenRepository, gameRepository),
    )
}