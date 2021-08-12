package com.bol.gmarchini.kalaha.service

import com.bol.gmarchini.kalaha.domain.GameManager
import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.KalahaGameRepository
import com.bol.gmarchini.kalaha.persistence.entity.KalahaGameEntity
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
    val mapper: KalahaGameMapper,
    val gameManager: GameManager,
    @Value("\${kalaha.pit.size}") val pitSize: Int,
    @Value("\${kalaha.pit.initialStones}") val initialStones: Int
) {
    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KalahaGameService::class.java)
    }

    /**
     * Creates a new Kalaha game.
     */
    fun create(): KalahaGame {
        logger.info("Creating a new Kalaha game")
        val newGame: KalahaGameEntity = KalahaGameEntity(
            currentPlayer = Side.SOUTH,
            southernPits = IntArray(pitSize) { initialStones },
            northernPits = IntArray(pitSize) { initialStones },
            southernKalaha = 0,
            northernKalaha = 0
        )

        val savedGame: KalahaGameEntity = this.repository.save(newGame)
        logger.info("Created Kalaha game. Id: ${savedGame.id}")

        return this.mapper.toDomain(savedGame)
    }

    /**
     * Gets all Kalaha Games.
     */
    fun getAll(): List<KalahaGame> {
        logger.info("Fetching all Kalaha games")
        return this.repository.findAll().map {
            this.mapper.toDomain(it)
        }
    }

    /**
     * Gets a Kalaha Game.
     * @param gameId the game id
     * @return The Kalaha Game DTO. Null if the kalaha is not found
     */
    fun getById(gameId: Int): KalahaGame {
        logger.info("Getting Kalaha game with id $gameId")
        val game: KalahaGameEntity? = this.repository.findByIdOrNull(gameId)
        checkNotNull(game) { throw GameNotFoundException(gameId) }
        return this.mapper.toDomain(game)
    }

    /**
     * Generates a movement for the given game.
     * @param gameId the game id
     * @param pitPosition a zero based pit position to generate the move
     * @return the Kalaha Game DTO after the movement. Null if the game is not found
     */
    fun move(
        gameId: Int,
        pitPosition: Int,
        requestingPlayer: String
    ): KalahaGame {
        val game: KalahaGame = this.getById(gameId)
        logger.info("[$gameId] executing move. Current player=[${game.currentPlayer}], pitPosition=[$pitPosition]")
        gameManager.move(game, pitPosition)
        this.repository.save(this.mapper.toEntity(game))

        return game
    }
}