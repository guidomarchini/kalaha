package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import com.helger.commons.annotation.VisibleForTesting

/**
 * Manages a Kalaha game.
 */
class KalahaGame private constructor(
    val table: Table,
    var currentPlayer: Side,
    val movementManager: MovementManager
) {
    /**
     * Kalaha game builders
     */
    companion object {
        private const val DEFAULT_PITS: Int = 6
        private const val DEFAULT_STONES: Int = 6

        /**
         * Creates a default Kalaha game with:
         * a table of default (6) pits and stones,
         * starting player SOUTH and
         * a default movement manager.
         */
        fun new(): KalahaGame {
            val table = Table.new(
                pitSize = DEFAULT_PITS,
                initialStones = DEFAULT_STONES
            )
            return KalahaGame(
                table = table,
                currentPlayer = Side.SOUTH,
                movementManager = MovementManager(table)
            )
        }

        /**
         * Creates a customized Kalaha game,
         * with a given table and movement manager.
         */
        @VisibleForTesting
        fun customizedGame(
            table: Table,
            currentPlayer: Side,
            movementManager: MovementManager
        ) = KalahaGame(
            table,
            currentPlayer,
            movementManager
        )
    }

    /**
     * A game is over when the current player have all its pits empty.
     */
    fun isGameOver(): Boolean =
        this.table.getPits(this.currentPlayer).all { it == 0 }

    /**
     * Returns the current winner of the game.
     * Null for tied game.
     */
    fun winner(): Side? = when(this.table.getKalaha(Side.SOUTH).compareTo(this.table.getKalaha(Side.NORTH))) {
        1 -> Side.SOUTH
        -1 -> Side.NORTH
        else -> null
    }

    /**
     * Make a move for the current player (delegated to movement manager)
     * After the movement, the current player is switched,
     * and checks for a game over.
     *
     * @param pitPosition zero based pit position to make the move
     */
    fun move(pitPosition: Int) {
        this.movementManager.move(pitPosition, playerSide = this.currentPlayer)
        this.switchPlayer()
        if (this.isGameOver()) {
            this.gameOver()
        }
    }

    /**
     * On game over the current player has no stones, hence no moves left.
     * This function picks all stones left from the opponent and moves them to its Kalaha.
     */
    private fun gameOver(): Unit {
        val opponentsSide: Side = this.currentPlayer.opposite()
        val pitsToClean: MutableList<Int> = this.table.getPits(opponentsSide)
        val stonesLeft = (0 until pitsToClean.size).fold(0){ acc, index ->
            val result: Int = acc + pitsToClean[index]
            pitsToClean[index] = 0
            result
        }

        this.table.kalahas[opponentsSide] = this.table.getKalaha(opponentsSide) + stonesLeft
    }


    private fun switchPlayer(): Unit {
        this.currentPlayer = this.currentPlayer.opposite()
    }
}