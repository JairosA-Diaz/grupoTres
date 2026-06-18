package com.example.grupotres.repository

import com.example.grupotres.data.User
import com.example.grupotres.data.UserDao

class UserRepository(private val userDao: UserDao) {
    suspend fun getUser(email: String, password: String): User? {
        return userDao.getUser(email, password)
    }
}