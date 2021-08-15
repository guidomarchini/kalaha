package com.bol.gmarchini.kalaha.utils

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.entity.KalahaGameEntity

object KalahaGameEntityBuilder {
    fun sampleKalahaGameEntity (
        id: Int? = null,
        currentSide: Side = Side.SOUTH,
        southernPlayer: String = "southernPlayer",
        northernPlayer: String = "northernPlayer",
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
            southernPits = southernPits,
            northernPits = northernPits,
            southernKalaha = southernKalaha,
            northernKalaha = northernKalaha
        )
}