package com.bol.gmarchini.kalaha.persistence

import com.bol.gmarchini.kalaha.entity.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Int> {
    fun getByUsername(username: String): User?
}