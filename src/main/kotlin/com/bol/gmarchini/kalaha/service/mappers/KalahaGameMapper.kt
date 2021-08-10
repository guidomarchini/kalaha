package com.bol.gmarchini.kalaha.service.mappers

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
     * Maps a stored Kalaha Game into a Kalaha Game.
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
     * Maps a Kalaha Game into a Kalaha Game Entity.
     * The id is null for games that aren't stored.
     */
    fun toEntity(kalahaGame: KalahaGame, id: Int? = null): KalahaGameEntity =
        KalahaGameEntity(
            id = id,
            currentPlayer = kalahaGame.currentPlayer,
            southernPits = kalahaGame.table.getPits(Side.SOUTH).toIntArray(),
            northernPits = kalahaGame.table.getPits(Side.NORTH).toIntArray(),
            southernKalaha = kalahaGame.table.getKalaha(Side.SOUTH),
            northernKalaha = kalahaGame.table.getKalaha(Side.NORTH),
            ended = kalahaGame.isGameOver()
        )
}