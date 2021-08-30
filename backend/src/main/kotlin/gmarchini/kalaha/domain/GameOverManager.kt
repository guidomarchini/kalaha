package gmarchini.kalaha.domain

import gmarchini.kalaha.model.KalahaGame
import gmarchini.kalaha.model.Side
import org.springframework.stereotype.Component

/**
 * GameOverManager has the rules of the game over.
 */
@Component
class GameOverManager {
    /**
     * A game is over when the current player have all its pits empty.
     */
    fun isGameOver(game: KalahaGame): Boolean =
        game.table.getPits(game.currentSide).all { it == 0 }

    /**
     * Checks for a game over scenario for the current player (no more rocks on the pits = no moves left)
     * In that case, moves all the opponent's rocks into its Kalaha.
     */
    fun checkForGameOver(game: KalahaGame): Unit {
        if (isGameOver(game))
            handleGameOver(game)
    }

    /**
     * On game over the current player has no stones, hence no moves left.
     * This function picks all stones left from the opponent and moves them to its Kalaha.
     */
    private fun handleGameOver(game: KalahaGame): Unit {
        val opponentsSide: Side = game.currentSide.opposite()
        val pitsToClean: MutableList<Int> = game.table.getPits(opponentsSide)
        // sets all pits to 0,
        val stonesLeft = (0 until pitsToClean.size).fold(0){ acc, index ->
            val result: Int = acc + pitsToClean[index]
            pitsToClean[index] = 0
            result
        }

        game.table.addStonesToKalaha(opponentsSide, stonesLeft)
        game.gameOver();
    }
}