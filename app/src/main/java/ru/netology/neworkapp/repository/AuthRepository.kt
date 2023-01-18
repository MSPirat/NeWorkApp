//package ru.netology.neworkapp.repository
//
//import ru.netology.neworkapp.api.ApiService
//import ru.netology.neworkapp.dto.User
//import ru.netology.neworkapp.errors.ApiError
//import ru.netology.neworkapp.errors.NetworkError
//import ru.netology.neworkapp.errors.UnknownError
//import java.io.IOException
//import javax.inject.Inject
//
//class AuthRepository @Inject constructor(
//    private val api: ApiService,
//) {
//    suspend fun authUser(login: String, password: String): User {
//        try {
//            val response = api.updateUser(login, password)
//            if (!response.isSuccessful) {
//                throw ApiError(response.message())
//            }
//            return response.body() ?: throw Exception()
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }
//
//    suspend fun registrationUser(login: String, password: String, name: String): User {
//        try {
//            val response = api.registrationUser(login, password, name)
//            if (!response.isSuccessful) {
//                throw ApiError(response.message())
//            }
//            return response.body() ?: throw Exception()
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }
//}