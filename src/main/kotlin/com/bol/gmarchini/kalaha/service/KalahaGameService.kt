package com.bol.gmarchini.kalaha.service

import com.bol.gmarchini.kalaha.application.dto.KalahaGameDto
import com.bol.gmarchini.kalaha.domain.KalahaGame
import com.bol.gmarchini.kalaha.persistence.KalahaGameRepository
import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
import com.bol.gmarchini.kalaha.service.exceptions.GameNotFoundException
import com.bol.gmarchini.kalaha.service.mappers.KalahaGameMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * Kalaha Game Service.
 * Provides the application functionality.
 */
@Service
class KalahaGameService @Autowired constructor(
    val repository: KalahaGameRepository,
    val mapper: KalahaGameMapper
) {
    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KalahaGameService::class.java)
    }

    /**
     * Creates a new Kalaha game.
     */
    fun create(): KalahaGameDto {
        logger.info("Creating a new Kalaha game")
        val newGame: KalahaGame = KalahaGame.new()

        val kalahaGameEntity: KalahaGameEntity = this.repository.save(
            this.mapper.toEntity(newGame)
        )

        return this.mapper.toApplication(kalahaGameEntity)
    }

    /**
     * Gets all Kalaha Games, ordered by ongoing first.
     */
    fun getAll(): List<KalahaGameDto> {
        logger.info("Fetching all Kalaha games")
        return this.repository.getAllByOrderByEnded().map {
            this.mapper.toApplication(it)
        }
    }

    /**
     * Gets a Kalaha Game.
     * @param gameId the game id
     * @return The Kalaha Game DTO. Null if the kalaha is not found
     */
    fun getById(gameId: Int): KalahaGameDto {
        logger.info("Getting Kalaha game with id $gameId")
        val entity: KalahaGameEntity? = this.repository.findByIdOrNull(gameId)
        checkNotNull(entity) { throw GameNotFoundException(gameId) }
        return this.mapper.toApplication(entity)
    }

    /**
     * Generates a movement for the given game.
     * @param gameId the game id
     * @param pitPosition a zero based pit position to generate the move
     * @return the Kalaha Game DTO after the movement. Null if the game is not found
     */
    fun move(
        gameId: Int,
        pitPosition: Int
    ): KalahaGameDto {
        val entity: KalahaGameEntity? = this.repository.findByIdOrNull(gameId)
        checkNotNull(entity) { throw GameNotFoundException(gameId) }

        val game: KalahaGame = this.mapper.toDomain(entity)
        logger.info("[$gameId] executing move. Current player=[${game.currentPlayer}], pitPosition=[$pitPosition]")
        game.move(pitPosition)
        val updatedGame: KalahaGameEntity = this.repository.save(this.mapper.toEntity(game, gameId))

        return this.mapper.toApplication(updatedGame)
    }
}