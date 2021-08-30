package gmarchini.kalaha.entity

import org.apache.commons.codec.digest.DigestUtils
import javax.persistence.*

@Entity
data class User private constructor (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val username: String,
    val passwordHash: String,
    @Enumerated(EnumType.STRING)
    val role: Role
) {
    companion object {
        fun new(
            id: Int? = null,
            username: String,
            password: String,
            role: Role
        ): User = User(
            id = id,
            username = username,
            passwordHash = DigestUtils.sha1Hex(password),
            role = role
        )
    }

    fun checkPassword(password: String): Boolean =
        DigestUtils.sha1Hex(password) == this.passwordHash
}