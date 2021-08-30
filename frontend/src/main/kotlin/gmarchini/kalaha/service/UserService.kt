package gmarchini.kalaha.service

import gmarchini.kalaha.entity.Role
import gmarchini.kalaha.entity.User
import gmarchini.kalaha.persistence.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (private val userRepository: UserRepository) {
    fun create(
        username: String,
        password: String,
        role: Role
    ): User {
        if (this.getUser(username) != null) {
            throw UserAlreadyExistsException(username)
        }
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            throw EmptyUserException()
        }

        return this.userRepository.save(User.new(username = username, password = password, role = role))
    }

    fun getUser(username: String): User? =
        this.userRepository.getByUsername(username)

    fun findAll(): List<User> =
        this.userRepository.findAll().toList()
}