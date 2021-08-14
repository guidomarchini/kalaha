package com.bol.gmarchini.kalaha

import com.bol.gmarchini.kalaha.front.entity.Role
import com.bol.gmarchini.kalaha.front.entity.User
import com.bol.gmarchini.kalaha.front.persistence.UserRepository
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