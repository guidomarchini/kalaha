package com.bol.gmarchini.kalaha.service.mappers

import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
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
            currentSide = kalahaGameEntity.currentSide,
            southernPlayer = kalahaGameEntity.southernPlayer,
            northernPlayer = kalahaGameEntity.northernPlayer,
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
            currentSide = kalahaGame.currentSide,
            southernPlayer = kalahaGame.southernPlayer,
            northernPlayer = kalahaGame.northernPlayer,
            southernPits = kalahaGame.table.getPits(Side.SOUTH).toIntArray(),
            northernPits = kalahaGame.table.getPits(Side.NORTH).toIntArray(),
            southernKalaha = kalahaGame.table.getKalaha(Side.SOUTH),
            northernKalaha = kalahaGame.table.getKalaha(Side.NORTH)
        )
}