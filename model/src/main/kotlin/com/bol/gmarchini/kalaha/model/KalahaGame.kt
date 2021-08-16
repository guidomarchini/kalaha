package com.bol.gmarchini.kalaha.model

data class KalahaGame (
    val id: Int? = null,
    var currentSide: Side,
    val southernPlayer: String,
    val northernPlayer: String,
    val table: Table
) {
    fun switchPlayer(): Unit {
        this.currentSide = this.currentSide.opposite()
    }

    fun currentPlayer(): String = when(this.currentSide) {
        Side.SOUTH -> southernPlayer
        Side.NORTH -> northernPlayer
    }
}