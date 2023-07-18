package ru.kheynov.secretsanta.domain.use_cases.users

import ru.kheynov.secretsanta.domain.entities.UserInfo
import ru.kheynov.secretsanta.domain.repositories.UsersRepository
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.TokenRepository
import ru.kheynov.secretsanta.utils.UserNotExistsException
import javax.inject.Inject

class GetSelfInfoUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
) {
    suspend operator fun invoke(): Resource<UserInfo> {
        val userCheck = usersRepository.checkUserRegistered()
        if (userCheck is Resource.Success && !userCheck.result)
            return Resource.Failure(UserNotExistsException())
        return usersRepository.getSelfInfo()
    }
}