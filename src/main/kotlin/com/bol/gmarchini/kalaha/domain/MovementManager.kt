package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.exceptions.InvalidMovementException
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table

/**
 * Manages table movements
 */
class MovementManager constructor(private val table: Table) {
    /**
     * Makes a move from the given player.
     * A normal move would be one in which the player has stones,
     *  and in that case the next pits will be filled with one stone each,
     *  until no more stones can be moved.
     *  If a Kalaha is reached, there are two possible scenarios:
     *   1) Current player's Kalaha: a stone is left there.
     *   2) Opponent's Kalaha: It's ignored
     * A special scenario is when we move just one stone into an empty pit:
     *  In that case, that stone and all of the stones in the opposite pit
     *  are moved into the current player's Kalaha.
     *
     * @param pitPosition zero based pit position to make the move
     * @param playerSide the current player's Side
     * @throws InvalidMovementException if the pit at the given position has no rocks
     */
    fun move(
        pitPosition: Int,
        playerSide: Side
    ): Table {
        if (this.table.getPits(playerSide)[pitPosition] == 0) {
            throw InvalidMovementException()
        }

        if (this.isSpecialMove(pitPosition, playerSide)) {
            this.doSpecialMove(pitPosition, playerSide)
        } else {
            this.doNormalMove(pitPosition, playerSide)
        }

        return this.table
    }

    /**
     * A special move is the one in which the player has 1 stone in a pit
     * and the next pit is empty
     */
    private fun isSpecialMove(
        pitPosition: Int,
        playerSide: Side
    ): Boolean {
        val currentPit = this.table.getPits(playerSide)

        val isLastPit: Boolean = currentPit.size == pitPosition
        val hasOneRock: Boolean = currentPit[pitPosition] == 1
        val hasEmptyNextPit: Boolean by lazy { currentPit[pitPosition + 1] == 0 }

        return !isLastPit && hasOneRock && hasEmptyNextPit
    }

    /**
     * Moves the stone from the current position and its opposite pit stack
     * to the current player's Kalaha.
     */
    private fun doSpecialMove(
        pitPosition: Int,
        playerSide: Side
    ): Unit {
        val currentPit: MutableList<Int> = this.table.getPits(playerSide)
        val oppositePit: MutableList<Int> = this.table.getPits(playerSide.opposite())

        // where do we rob?
        // pitsSize-1 is the last place. PitPosition+1 is the count from left to right.
        // pitsSize - 1 - (pitPosition+1) = pitSize - pitPosition - 2
        val indexToRob = oppositePit.size - pitPosition - 2
        val robbedStones: Int = oppositePit[indexToRob]
        oppositePit[indexToRob] = 0
        currentPit[pitPosition] = 0

        this.table.kalahas[playerSide] = this.table.getKalaha(playerSide) + 1 + robbedStones
    }

    /**
     * Normal move, leaving one stone for each following pit in a circular way,
     * leaving stones for the current player's Kalaha.
     */
    private fun doNormalMove(
        pitPosition: Int,
        playerSide: Side
    ): Unit {
        var currentSide: Side = playerSide
        var currentPits: MutableList<Int> = this.table.getPits(playerSide)
        var stonesToMove: Int = currentPits[pitPosition]
        currentPits[pitPosition] = 0
        var nextPosition: Int = pitPosition + 1

        while(stonesToMove > 0) { // move all rocks

            if (nextPosition >= currentPits.size) { // reached the end of the pits
                if (currentSide == playerSide) { // player's Kalaha
                    this.table.kalahas[currentSide] = this.table.getKalaha(currentSide) + 1
                    stonesToMove--
                } // else is opponent's Kalaha, so we ignore the movement
                // change sides
                currentSide = currentSide.opposite()
                currentPits = this.table.getPits(currentSide)
                nextPosition = 0

            } else { // pit
                currentPits[nextPosition] += 1

                nextPosition++
                stonesToMove--
            }
        }
    }
}