package gmarchini.kalaha.service

import gmarchini.kalaha.domain.GameManager
import gmarchini.kalaha.domain.exceptions.InvalidMovementException
import gmarchini.kalaha.model.KalahaGame
import gmarchini.kalaha.model.Side
import gmarchini.kalaha.model.Table
import gmarchini.kalaha.persistence.KalahaGameRepository
import gmarchini.kalaha.persistence.entity.KalahaGameEntity
import gmarchini.kalaha.service.exceptions.GameNotFoundException
import gmarchini.kalaha.service.mappers.KalahaGameMapper
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
    @Value("\${gmarchini.kalaha.pit.size}") val pitSize: Int,
    @Value("\${gmarchini.kalaha.pit.initialStones}") val initialStones: Int
) {
    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KalahaGameService::class.java)
    }

    /**
     * Creates a new Kalaha game for the given Users' usernames
     * @param southernPlayer the southern player's username
     * @param northernPlayer the northern player's username
     */
    fun create(
        southernPlayer: String,
        northernPlayer: String
    ): KalahaGame {
        logger.info("Creating a new Kalaha game")
        val newGame: KalahaGame = KalahaGame(
            currentSide = Side.SOUTH,
            southernPlayer = southernPlayer,
            northernPlayer = northernPlayer,
            table = Table(
                southernPits = MutableList(pitSize) { initialStones },
                northernPits = MutableList(pitSize) { initialStones },
                southernKalaha = 0,
                northernKalaha = 0
            )
        )

        val asEntity = this.mapper.toEntity(newGame)
        val savedGame: KalahaGameEntity = this.repository.save(asEntity)
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
     * Gets all the games being player for the given username.
     * @param username the User's username
     */
    fun getGamesOfPlayer(username: String): List<KalahaGame> {
        logger.info("Fetching all games of player $username")
        return this.repository.getGamesFromPlayer(username).map {
            this.mapper.toDomain(it)
        }
    }

    /**
     * Gets a Kalaha Game.
     * @param gameId the game id
     * @return The Kalaha Game DTO. Null if the gmarchini.kalaha is not found
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
        executingPlayer: String
    ): KalahaGame {
        val game: KalahaGame = this.getById(gameId)
        logger.info("[$gameId] executing move. Current player=[${game.currentSide}], pitPosition=[$pitPosition]")
        if (game.currentPlayer() != executingPlayer) {
            throw InvalidMovementException()
        }

        gameManager.move(game, pitPosition)
        this.repository.save(this.mapper.toEntity(game))

        return game
    }
}