package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.domain.exceptions.InvalidMovementException
import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import org.springframework.stereotype.Component

/**
 * MovementManager has the movements rules.
 */
@Component
class MovementManager {
    /**
     * Makes a move from the given player.
     * A normal move would be one in which the player has stones,
     *  and in that case the next pits will be filled with one stone each,
     *  until no more stones can be moved.
     *  If a Kalaha is reached, there are two possible scenarios:
     *   1) Current player's Kalaha: a stone is left there.
     *   2) Opponent's Kalaha: It's ignored
     * A special scenario is when the last stone falls into a Kalaha or empty pit:
     *  Empty pit: that stone and all of the stones in the opposite pit
     *      are moved into the current player's Kalaha.
     *  Kalaha: The player takes another turn
     *
     * @param game the game where the movement is going to be made.
     * @param pitPosition zero based pit position to make the move.
     * @throws InvalidMovementException if the pit at the given position has no rocks.
     */
    fun move(
        game: KalahaGame,
        pitPosition: Int
    ): Unit {
        if (game.table.getPits(game.currentPlayer)[pitPosition] == 0) {
            throw InvalidMovementException()
        }

        val (lastStoneSide: Side, lastPosition: Int) = this.moveStones(game, pitPosition)
        this.applySpecialRules(game, lastStoneSide, lastPosition)
    }

    /**
     * Applies special rules after the movement is done.
     * Special rules are:
     * * If last stone landed on owner's kalaha, then it takes another turn
     * * If last stone landed on owner's empty pit, then moves that stone
     *      and all of the opposite pit stones into owner's kalaha
     *
     * @param game The Kalaha game where the movement was done.
     * @param lastStoneSide the side where the last stone was deposited.
     * @param lastPosition the position where the last stone was deposited.
     */
    private fun applySpecialRules(
        game: KalahaGame,
        lastStoneSide: Side,
        lastPosition: Int
    ) {
        val sameSideAsPlayer: Boolean = game.currentPlayer == lastStoneSide
        val isKalaha: Boolean = isKalaha(game, lastStoneSide, lastPosition)
        val lastStoneLandedOnEmptyPit: Boolean = !isKalaha && game.table.getPits(lastStoneSide)[lastPosition] == 1

        // need to rob stones?
        if (sameSideAsPlayer && lastStoneLandedOnEmptyPit) {
            this.robStones(game.table, lastStoneSide, lastPosition)
        }

        // !(last stone in owner's kalaha -> take another turn)
        if (!isKalaha(game, lastStoneSide, lastPosition)) {
            game.switchPlayer()
        }
    }

    /**
     * Moves the stone from the current position and its opposite pit stack
     * to the current player's Kalaha.
     */
    private fun robStones(
        table: Table,
        playerSide: Side,
        pitPosition: Int
    ): Unit {
        val currentPit: MutableList<Int> = table.getPits(playerSide)
        val oppositePit: MutableList<Int> = table.getPits(playerSide.opposite())

        // where do we rob? pits are mirrored, so...
        val indexToRob = oppositePit.size - 1 - pitPosition
        val robbedStones: Int = oppositePit[indexToRob]
        oppositePit[indexToRob] = 0
        currentPit[pitPosition] = 0

        table.addStonesToKalaha(playerSide, robbedStones+1)
    }

    /**
     * Executes the current move.
     * It returns a Pair with side and position that the last stone landed.
     * @param game The Kalaha game to execute the movement.
     * @param pitPosition the pit position to execute the movement.
     */
    fun moveStones(
        game: KalahaGame,
        pitPosition: Int
    ): Pair<Side, Int> {
        val currentSide: Side = game.currentPlayer
        val currentPits: MutableList<Int> = game.table.getPits(currentSide)
        val nextPosition: Int = pitPosition + 1
        val stonesToMove: Int = currentPits[pitPosition]
        currentPits[pitPosition] = 0

        return innerMove(game, currentSide, nextPosition, stonesToMove)
    }

    private fun innerMove(
        game: KalahaGame,
        currentSide: Side,
        currentPosition: Int,
        stonesLeft: Int
    ): Pair<Side, Int> {
        val isKalaha = currentPosition == game.table.getPits(currentSide).size

        if (isKalaha && currentSide != game.currentPlayer) {
            // kalaha reached, ignore current movement
            return innerMove(game, currentSide.opposite(), 0, stonesLeft)
        }

        if (isKalaha) {
            game.table.addStonesToKalaha(currentSide)
        } else {
            game.table.getPits(currentSide)[currentPosition] += 1
        }

        return if(stonesLeft-1 == 0) {
            Pair(currentSide, currentPosition)
        } else {
            val nextSide = if (isKalaha) currentSide.opposite() else currentSide
            val nextPosition = if (isKalaha) 0 else currentPosition + 1
            innerMove(game, nextSide, nextPosition, stonesLeft = stonesLeft - 1)
        }
    }

    private fun isKalaha(
        game: KalahaGame,
        side: Side,
        currentPosition: Int
    ) = currentPosition == game.table.getPits(side).size
}