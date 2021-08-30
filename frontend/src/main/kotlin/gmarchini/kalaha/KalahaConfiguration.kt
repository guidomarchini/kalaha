package gmarchini.kalaha

import gmarchini.kalaha.entity.Role
import gmarchini.kalaha.entity.User
import gmarchini.kalaha.persistence.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Application startup configuration
 */
@Configuration
class KalahaConfiguration {
    @Bean
    fun dataInitializer(
        userRepository: UserRepository
    ) = ApplicationRunner {
        userRepository.save(User.new(username = "user", password = "u", role = Role.USER))
        userRepository.save(User.new(username = "admin", password = "a", role = Role.ADMIN))
    }
}