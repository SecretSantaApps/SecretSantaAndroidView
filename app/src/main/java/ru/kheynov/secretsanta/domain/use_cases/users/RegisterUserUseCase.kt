package ru.kheynov.secretsanta.domain.use_cases.users

import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val usersRepository: UsersRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        tokenRepository.fetchToken()
        return usersRepository.deleteUser()
    }
}