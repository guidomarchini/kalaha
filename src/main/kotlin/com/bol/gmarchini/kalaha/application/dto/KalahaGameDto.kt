package com.bol.gmarchini.kalaha.application.dto

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Winner

data class KalahaGameDto(
    val id: Int,
    val currentPlayer: Side,

    // table properties
    val southernPits: List<Int>,
    val northernPits: List<Int>,
    val southernKalaha: Int,
    val northernKalaha: Int,

    // additional properties
    val ended: Boolean,
    val winner: Winner?
)
