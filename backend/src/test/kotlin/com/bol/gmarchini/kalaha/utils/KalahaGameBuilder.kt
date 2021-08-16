package com.bol.gmarchini.kalaha.utils

import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import com.bol.gmarchini.kalaha.model.TableBuilder

object KalahaGameBuilder {
    fun sampleKalahaGame (
        id: Int = 0,
        currentPlayer: Side = Side.SOUTH,
        southernPlayer: String = "southernPlayer",
        northernPlayer: String = "northernPlayer",
        table: Table = TableBuilder.sampleTable()
    ): KalahaGame =
        KalahaGame(
            id = id,
            currentSide = currentPlayer,
            southernPlayer = southernPlayer,
            northernPlayer = northernPlayer,
            table = table
        )
}