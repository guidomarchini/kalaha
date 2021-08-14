package com.bol.gmarchini.kalaha.front.persistence

import com.bol.gmarchini.kalaha.front.entity.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Int> {
    fun getByUsername(username: String): User?
}