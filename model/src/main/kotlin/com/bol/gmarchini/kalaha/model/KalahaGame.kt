package com.bol.gmarchini.kalaha.model

import java.util.*

data class KalahaGame (
    val id: Int? = null,
    var currentSide: Side,
    val southernPlayer: String,
    val northernPlayer: String,
    val table: Table,
    val createdDate: Date = Date(),
    var ended: Boolean = false
) {
    fun switchPlayer(): Unit {
        this.currentSide = this.currentSide.opposite()
    }

    fun currentPlayer(): String = when(this.currentSide) {
        Side.SOUTH -> southernPlayer
        Side.NORTH -> northernPlayer
    }

    fun gameOver(): Unit {
        this.ended = true
    }
}