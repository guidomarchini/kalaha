package com.bol.gmarchini.kalaha.model

import javax.persistence.*

data class KalahaGame (
    var id: Int? = null,
    var currentPlayer: Side,
    var table: Table
) {
    fun switchPlayer(): Unit {
        this.currentPlayer = this.currentPlayer.opposite()
    }
}