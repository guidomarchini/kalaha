package com.bol.gmarchini.kalaha.service.mappers

import com.bol.gmarchini.kalaha.application.dto.KalahaGameDto
import com.bol.gmarchini.kalaha.domain.GameOverManager
import com.bol.gmarchini.kalaha.domain.KalahaGame
import com.bol.gmarchini.kalaha.domain.MovementManager
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
import org.springframework.stereotype.Component

/**
 * Kalaha game mapper.
 * Used to transform a game into a different layer object.
 */
@Component
class KalahaGameMapper {

    /**
     * Maps a Kalaha game to its application layer representation.
     */
    fun toApplication(kalahaGame: KalahaGame, id: Int): KalahaGameDto =
        KalahaGameDto(
            id = id,
            currentPlayer = kalahaGame.currentPlayer,
            southernPits = kalahaGame.table.getPits(Side.SOUTH),
            northernPits = kalahaGame.table.getPits(Side.NORTH),
            southernKalaha = kalahaGame.table.getKalaha(Side.SOUTH),
            northernKalaha = kalahaGame.table.getKalaha(Side.NORTH),
            ended = kalahaGame.isGameOver(),
            winner = if (kalahaGame.isGameOver()) kalahaGame.getWinner() else null
        )

    /**
     * Maps a Kalaha game entity to its application layer representation.
     */
    fun toApplication(kalahaGameEntity: KalahaGameEntity): KalahaGameDto =
        KalahaGameDto(
            id = kalahaGameEntity.id!!,
            currentPlayer = kalahaGameEntity.currentPlayer,
            southernPits = kalahaGameEntity.southernPits.toList(),
            northernPits = kalahaGameEntity.northernPits.toList(),
            southernKalaha = kalahaGameEntity.southernKalaha,
            northernKalaha = kalahaGameEntity.northernKalaha,
            ended = kalahaGameEntity.ended,
            winner = kalahaGameEntity.winner
        )

    /**
     * Maps a Kalaha Game Entity to its domain representation.
     */
    fun toDomain(kalahaGameEntity: KalahaGameEntity): KalahaGame =
        KalahaGame.restore(
            table = Table.restore(
                southernPits = kalahaGameEntity.southernPits.toMutableList(),
                northernPits = kalahaGameEntity.northernPits.toMutableList(),
                southernKalaha = kalahaGameEntity.southernKalaha,
                northernKalaha = kalahaGameEntity.northernKalaha
            ),
            currentPlayer = kalahaGameEntity.currentPlayer,
            movementManager = MovementManager(),
            gameOverManager = GameOverManager()
        )

    /**
     * Maps a Kalaha Game into its entity representation.
     */
    fun toEntity(kalahaGame: KalahaGame, id: Int? = null): KalahaGameEntity =
        KalahaGameEntity(
            id = id,
            currentPlayer = kalahaGame.currentPlayer,
            southernPits = kalahaGame.table.getPits(Side.SOUTH).toIntArray(),
            northernPits = kalahaGame.table.getPits(Side.NORTH).toIntArray(),
            southernKalaha = kalahaGame.table.getKalaha(Side.SOUTH),
            northernKalaha = kalahaGame.table.getKalaha(Side.NORTH),
            ended = kalahaGame.isGameOver(),
            winner = if(kalahaGame.isGameOver()) kalahaGame.getWinner() else null
        )
}