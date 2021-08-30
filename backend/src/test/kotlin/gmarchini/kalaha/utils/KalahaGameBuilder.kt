package gmarchini.kalaha.utils

import gmarchini.kalaha.model.KalahaGame
import gmarchini.kalaha.model.Side
import gmarchini.kalaha.model.Table
import gmarchini.kalaha.model.TableBuilder

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