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
import ru.kheynov.secretsanta.domain.use_cases.UseCases
import ru.kheynov.secretsanta.utils.AuthInterceptor
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseURL() = "https://santa.s.kheynov.ru/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    fun provideFirebaseInstance() = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthUIInstance() = AuthUI.getInstance()

    @Provides
    fun provideOkHttpClient(
        firebaseAuth: FirebaseAuth,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(firebaseAuth))
            .build()

    @Provides
    fun provideUseCases() = UseCases

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String, httpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BASE_URL)
            .client(httpClient)
            .build()
    }
}