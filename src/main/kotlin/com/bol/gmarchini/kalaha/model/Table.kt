package com.bol.gmarchini.kalaha.model

import com.helger.commons.annotation.VisibleForTesting

/**
 * The Kalaha table.
 * It contains two sides: South and North.
 * Each side has a Kalaha and a given number of pits.
 * We could say that the Kalaha is the current amount of points the player has.
 * Each pit can have any amount of stones.
 */
class Table private constructor (
    val pits: Map<Side, MutableList<Int>>,
    val kalahas: MutableMap<Side, Int>
) {
    companion object {
        /**
         * Generates a new table, with the given pit size and initial stones.
         * @param pitSize the amount of pits each side has
         * @param initialStones the amount of stones each pit has
         * @return a new Table
         */
        fun new(
            pitSize: Int,
            initialStones: Int
        ): Table {
            val pits: Map<Side, MutableList<Int>> = mapOf(
                Pair(
                    Side.SOUTH,
                    MutableList(pitSize) { initialStones }
                ),
                Pair(
                    Side.NORTH,
                    MutableList(pitSize) { initialStones }
                )
            )

            val kalahas: MutableMap<Side, Int> = mutableMapOf(
                Pair(Side.SOUTH, 0),
                Pair(Side.NORTH, 0)
            )

            return Table(
                pits = pits,
                kalahas = kalahas
            )
        }

        /**
         * Generates a customized table
         */
        @VisibleForTesting
        fun customized(
            southernPits: MutableList<Int>,
            northernPits: MutableList<Int>,
            southernKalaha: Int,
            northernKalaha: Int
        ): Table {
            val pits: Map<Side, MutableList<Int>> = mapOf(
                Pair(
                    Side.SOUTH,
                    southernPits
                ),
                Pair(
                    Side.NORTH,
                    northernPits
                )
            )

            val kalahas: MutableMap<Side, Int> = mutableMapOf(
                Pair(Side.SOUTH, southernKalaha),
                Pair(Side.NORTH, northernKalaha)
            )

            return Table(
                pits = pits,
                kalahas = kalahas
            )
        }
    }

    /**
     * Returns the pits from a given side.
     * We always have a pit for both sides.
     */
    fun getPits(side: Side): MutableList<Int> =
        this.pits[side]!!

    /**
     * Returns the Kalaha from a given side.
     * We always have a Kalaha for both sides.
     */
    fun getKalaha(side: Side): Int =
        this.kalahas[side]!!
}