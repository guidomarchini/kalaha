package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import com.bol.gmarchini.kalaha.model.Winner

/**
 * Manages a Kalaha game.
 */
class KalahaGame private constructor(
    val table: Table,
    var currentPlayer: Side,
    private val movementManager: MovementManager,
    private val gameOverManager: GameOverManager
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
                movementManager = MovementManager(),
                gameOverManager = GameOverManager()
            )
        }

        /**
         * Restores Kalaha game,
         * with a given table, player and movement manager.
         */
        fun restore(
            table: Table,
            currentPlayer: Side,
            movementManager: MovementManager,
            gameOverManager: GameOverManager
        ) = KalahaGame(
            table,
            currentPlayer,
            movementManager,
            gameOverManager
        )
    }

    /**
     * A game is over when the current player have all its pits empty.
     */
    fun isGameOver(): Boolean =
        this.gameOverManager.isGameOver(
            table = this.table,
            currentSide = this.currentPlayer
        )

    /**
     * Returns the current winner of the game.
     * Null for tied game.
     */
    fun getWinner(): Winner = when(this.table.getKalaha(Side.SOUTH).compareTo(this.table.getKalaha(Side.NORTH))) {
        1 -> Winner.SOUTH
        -1 -> Winner.NORTH
        else -> Winner.TIED
    }

    /**
     * Make a move for the current player (delegated to movement manager)
     * After the movement, the current player is switched,
     * and checks for a game over.
     *
     * @param pitPosition zero based pit position to make the move
     */
    fun move(pitPosition: Int) {
        this.movementManager.move(
            table = this.table,
            pitPosition = pitPosition,
            playerSide = this.currentPlayer
        )
        this.switchPlayer()
        this.gameOverManager.checkForGameOver(
            table = this.table,
            currentSide = this.currentPlayer
        )
    }

    private fun switchPlayer(): Unit {
        this.currentPlayer = this.currentPlayer.opposite()
    }
}