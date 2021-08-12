package com.bol.gmarchini.kalaha.service.mappers

import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Table
import com.bol.gmarchini.kalaha.persistence.entity.KalahaGameEntity
import org.springframework.stereotype.Component

/**
 * Kalaha game mapper.
 * Used to transform a game into a different layer object.
 */
@Component
class KalahaGameMapper {

    /**
     * Maps a Kalaha game to its domain layer representation.
     */
    fun toDomain(kalahaGameEntity: KalahaGameEntity): KalahaGame =
        KalahaGame(
            id = kalahaGameEntity.id,
            currentPlayer = kalahaGameEntity.currentPlayer,
            table = Table(
                southernPits = kalahaGameEntity.southernPits.toMutableList(),
                northernPits = kalahaGameEntity.northernPits.toMutableList(),
                southernKalaha = kalahaGameEntity.southernKalaha,
                northernKalaha = kalahaGameEntity.northernKalaha,
            )
        )

    /**
     * Maps a domain Kalaha Game to its entity representation
     */
    fun toEntity(kalahaGame: KalahaGame): KalahaGameEntity =
        KalahaGameEntity(
            id = kalahaGame.id,
            currentPlayer = kalahaGame.currentPlayer,
            southernPits = kalahaGame.table.southernPits.toIntArray(),
            northernPits = kalahaGame.table.northernPits.toIntArray(),
            southernKalaha = kalahaGame.table.southernKalaha,
            northernKalaha = kalahaGame.table.northernKalaha
        )
}