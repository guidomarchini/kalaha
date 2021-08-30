package gmarchini.kalaha.persistence

import gmarchini.kalaha.entity.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Int> {
    fun getByUsername(username: String): User?
}