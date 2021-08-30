package gmarchini.kalaha.service.mappers

import gmarchini.kalaha.model.KalahaGame
import gmarchini.kalaha.model.Side
import gmarchini.kalaha.model.Table
import gmarchini.kalaha.persistence.entity.KalahaGameEntity
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
            ended = kalahaGameEntity.ended,
            createdDate = kalahaGameEntity.createdDate,
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
            ended = kalahaGame.ended,
            createdDate = kalahaGame.createdDate,
            southernPits = kalahaGame.table.getPits(Side.SOUTH).toIntArray(),
            northernPits = kalahaGame.table.getPits(Side.NORTH).toIntArray(),
            southernKalaha = kalahaGame.table.getKalaha(Side.SOUTH),
            northernKalaha = kalahaGame.table.getKalaha(Side.NORTH)
        )
}