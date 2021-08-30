package gmarchini.kalaha.domain

import gmarchini.kalaha.model.KalahaGame
import gmarchini.kalaha.model.Side
import gmarchini.kalaha.model.Winner
import org.springframework.stereotype.Component

/**
 * Orchestrates the game flow.
 */
@Component
class GameManager constructor(
    private val movementManager: MovementManager,
    private val gameOverManager: GameOverManager
) {
    fun isGameOver(game: KalahaGame): Boolean =
        this.gameOverManager.isGameOver(game)

    /**
     * Returns the current winner of the game.
     * Null for tied game.
     */
    fun getWinner(game: KalahaGame): Winner = when(game.table.getKalaha(Side.SOUTH).compareTo(game.table.getKalaha(Side.NORTH))) {
        1 -> Winner.SOUTH
        -1 -> Winner.NORTH
        else -> Winner.TIED
    }

    /**
     * Make a move for the current player.
     * and checks for a game over.
     *
     * @param game game to
     * @param pitPosition zero based pit position to make the move
     */
    fun move(
        game: KalahaGame,
        pitPosition: Int
    ) {
        this.movementManager.move(game, pitPosition)
        this.gameOverManager.checkForGameOver(game)
    }
}