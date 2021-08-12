package com.bol.gmarchini.kalaha.model

object KalahaGameBuilder {
    fun sampleKalahaGame (
        id: Int = 0,
        currentPlayer: Side = Side.SOUTH,
        table: Table = TableBuilder.sampleTable()
    ): KalahaGame =
        KalahaGame(id, currentPlayer, table)
}