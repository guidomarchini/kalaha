package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table

class GameOverManager {
    /**
     * A game is over when the current player have all its pits empty.
     */
    fun isGameOver(
        table: Table,
        currentSide: Side
    ): Boolean =
        table.getPits(currentSide).all { it == 0 }

    /**
     * Checks for a game over scenario (no more rocks on the pits = no moves left)
     * In that case, moves all the opponent's rocks into its Kalaha.
     */
    fun checkForGameOver(
        table: Table,
        currentSide: Side
    ): Unit {
        if (isGameOver(table, currentSide))
            handleGameOver(table, currentSide)
    }

    /**
     * On game over the current player has no stones, hence no moves left.
     * This function picks all stones left from the opponent and moves them to its Kalaha.
     */
    private fun handleGameOver(
        table: Table,
        currentSide: Side
    ): Unit {
        val opponentsSide: Side = currentSide.opposite()
        val pitsToClean: MutableList<Int> = table.getPits(opponentsSide)
        val stonesLeft = (0 until pitsToClean.size).fold(0){ acc, index ->
            val result: Int = acc + pitsToClean[index]
            pitsToClean[index] = 0
            result
        }

        table.kalahas[opponentsSide] = table.getKalaha(opponentsSide) + stonesLeft
    }
}