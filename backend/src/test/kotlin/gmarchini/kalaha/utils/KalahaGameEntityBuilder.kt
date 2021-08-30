package gmarchini.kalaha.utils

import gmarchini.kalaha.model.Side
import gmarchini.kalaha.persistence.entity.KalahaGameEntity
import java.util.*

object KalahaGameEntityBuilder {
    fun sampleKalahaGameEntity (
        id: Int? = null,
        currentSide: Side = Side.SOUTH,
        southernPlayer: String = "southernPlayer",
        northernPlayer: String = "northernPlayer",
        ended: Boolean = false,
        createdDate: Date = Date(),
        southernPits: IntArray = intArrayOf(7, 7, 7),
        northernPits: IntArray = intArrayOf(7, 7, 7),
        southernKalaha: Int = 0,
        northernKalaha: Int = 0
    ): KalahaGameEntity =
        KalahaGameEntity(
            id = id,
            currentSide = currentSide,
            southernPlayer = southernPlayer,
            northernPlayer = northernPlayer,
            ended = ended,
            createdDate = createdDate,
            southernPits = southernPits,
            northernPits = northernPits,
            southernKalaha = southernKalaha,
            northernKalaha = northernKalaha
        )
}