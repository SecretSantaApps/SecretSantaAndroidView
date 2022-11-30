package ru.kheynov.secretsanta.domain.use_cases.users

import ru.kheynov.secretsanta.data.dto.RegisterUser
import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject


class RegisterUserUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val usersRepository: UsersRepository,
) {
    suspend operator fun invoke(
        user: RegisterUser,
    ): Resource<Unit> {
        tokenRepository.fetchToken()
        return usersRepository.registerUser(user)
    }
}