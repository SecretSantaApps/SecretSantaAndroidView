package ru.kheynov.secretsanta.domain.repositories

import retrofit2.HttpException
import ru.kheynov.secretsanta.data.api.UserAPI
import ru.kheynov.secretsanta.data.dto.RegisterUser
import ru.kheynov.secretsanta.data.dto.UpdateUser
import ru.kheynov.secretsanta.data.dto.UserInfo
import ru.kheynov.secretsanta.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepository @Inject constructor(private val userAPI: UserAPI) {
    suspend fun registerUser(user: RegisterUser): Resource<Unit> {
        return try {
            userAPI.registerUser(user)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun deleteUser(): Resource<Unit> {
        return try {
            userAPI.deleteUser()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun updateUser(updateRequest: UpdateUser): Resource<Unit> {
        return try {
            userAPI.updateUser(updateRequest)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun getSelfInfo(): Resource<UserInfo> {
        return try {
            val userInfo = userAPI.getSelfInfo()
            Resource.Success(userInfo)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend fun checkUserRegistered(): Resource<Boolean> {
        return try {
            userAPI.checkUserRegistered()
            Resource.Success(true) // user registered
        } catch (e: HttpException) {
            if (e.code() == 400)
                Resource.Success(false) // user not registered
            else
                Resource.Failure(e) // another error
        } catch (e: Exception) {
            Resource.Failure(e) // another error
        }
    }

    suspend fun getFirebaseName(): Resource<String> {
        return try {
            val res = userAPI.getFirebaseName()
            Resource.Success(res.username) // user registered
        } catch (e: Exception) {
            Resource.Failure(e) // another error
        }
    }
}