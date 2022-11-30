package ru.kheynov.secretsanta.domain.use_cases.users

import ru.kheynov.secretsanta.data.dto.UpdateUser
import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val usersRepository: UsersRepository,
) {
    suspend operator fun invoke(updateUser: UpdateUser): Resource<Unit> {
        tokenRepository.fetchToken()
        val userCheck = usersRepository.checkUserRegistered()
        if (userCheck is Resource.Success && !userCheck.result)
            return Resource.Failure(UserNotExistsException())
        return usersRepository.updateUser(updateUser)
    }
}