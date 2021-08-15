package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.domain.exceptions.InvalidMovementException
import com.bol.gmarchini.kalaha.model.*
import com.bol.gmarchini.kalaha.utils.KalahaGameBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MovementManagerTest {
    private val movementManager: MovementManager = MovementManager()

    @Nested
    inner class InvalidMovement {
        @Test
        fun `throws error`() {
            // arrange
            val playerSide: Side = Side.SOUTH
            val table: Table = TableBuilder.sampleTable(
                southernPits = mutableListOf(0),
                northernPits = mutableListOf(0)
            )
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(table = table, currentPlayer = playerSide)

            // act - assert
            assertThrows<InvalidMovementException> {
                movementManager.move(game, pitPosition = 0)
            }
        }
    }

    @Nested
    inner class NormalMove {
        @Test
        fun `move inside the pit`() {
            // arrange
            /**
             *          N
             *   | 6 | 6 | 6 | 6 | <-
             *   |---------------|
             * ->| 2 | 0 | 4 | 8 |
             *          S
             */

            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(2, 0, 4, 8),
                northernPits = mutableListOf(6, 6, 6, 6)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 0)

            // assert
            /**
             * Expected
             *          N
             *   | 6 | 6 | 6 | 6 | <-
             *   |---------------|
             * ->| 0 | 1 | 5 | 8 |
             *          S
             *  South Kalaha: 0
             *  North Kalaha: 0
             */
            assertThat(game.table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(0, 1, 5, 8))
            assertThat(game.table.getPits(Side.NORTH)).isEqualTo(mutableListOf(6, 6, 6, 6))
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(0)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer.opposite())
        }

        @Test
        fun `move cross the kalaha side`() {
            // arrange
            /**
             *          N
             *   | 6 | 6 | 6 | 6 | <-
             *   |---------------|
             * ->| 2 | 0 | 4 | 8 |
             *          S
             */

            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(2, 0, 4, 8),
                northernPits = mutableListOf(6, 6, 6, 6)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 2)

            // assert
            /**
             * Expected
             *          N
             *   | 6 | 6 | 7 | 7 | <-
             *   |---------------|
             * ->| 2 | 0 | 0 | 9 |
             *          S
             *  South Kalaha: 1
             *  North Kalaha: 0
             */
            assertThat(game.table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(2, 0, 0, 9))
            assertThat(game.table.getPits(Side.NORTH)).isEqualTo(mutableListOf(7, 7, 6, 6))
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer.opposite())
        }

        @Test
        fun `move cross the kalaha side twice`() {
            // arrange
            /**
             *          N
             *   | 6 | 6 | 6 | 6 | <-
             *   |---------------|
             * ->| 2 | 0 | 4 | 8 |
             *          S
             */

            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(2, 0, 4, 8),
                northernPits = mutableListOf(6, 6, 6, 6)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 3)

            // assert
            /**
             *          N
             *   | 7 | 7 | 7 | 7 | <-
             *   |---------------|
             * ->| 3 | 1 | 5 | 0 |
             *          S
             *  South Kalaha: 1
             *  North Kalaha: 0
             */
            assertThat(game.table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(3, 1, 5, 0))
            assertThat(game.table.getPits(Side.NORTH)).isEqualTo(mutableListOf(7, 7, 7, 7))
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer.opposite())
        }
    }

    @Nested
    inner class SpecialMovement {
        @Test
        fun `move with stones on the opposite side`() {
            // arrange
            /**
             *       N
             *   | 1 | 6 | <-
             *   |-------|
             * ->| 1 | 0 |
             *       S
             */
            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(1, 0),
                northernPits = mutableListOf(6, 1)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 0)

            // assert
            /**
             *       N
             *   | 1 | 0 | <-
             *   |-------|
             * ->| 0 | 0 |
             *       S
             *  South Kalaha: 7
             */
            assertThat(game.table.getPits(Side.SOUTH)).containsExactly(0, 0)
            assertThat(game.table.getPits(Side.NORTH)).containsExactly(0, 1)
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(7)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer.opposite())
        }

        @Test
        fun `move without stones on the opposite side`() {
            // arrange
            /**
             *       N
             *   | 1 | 0 | <-
             *   |-------|
             * ->| 1 | 0 |
             *       S
             */
            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(1, 0),
                northernPits = mutableListOf(0, 1)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 0)

            // assert
            /**
             *       N
             *   | 1 | 0 | <-
             *   |-------|
             * ->| 0 | 0 |
             *       S
             *  South Kalaha: 1
             */
            assertThat(game.table.getPits(Side.SOUTH)).containsExactly(0, 0)
            assertThat(game.table.getPits(Side.NORTH)).containsExactly(0, 1)
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer.opposite())
        }

        @Test
        fun `move a pile of two stones into an empty pit`() {
            // arrange
            /**
             *          N
             *   | 1 | 1 | 6 | <-
             *   |-----------|
             * ->| 2 | 1 | 0 |
             *          S
             *  South Kalaha: 7
             */
            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(2, 1, 0),
                northernPits = mutableListOf(6, 1, 1)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 0)

            // assert
            /**
             * Expected
             *          N
             *   | 1 | 1 | 0 | <-
             *   |-----------|
             * ->| 0 | 2 | 0 |
             *          S
             *  South Kalaha: 7
             */
            assertThat(game.table.getPits(Side.SOUTH)).containsExactly(0, 2, 0)
            assertThat(game.table.getPits(Side.NORTH)).containsExactly(0, 1, 1)
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(7)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer.opposite())
        }

        @Test
        fun `empty pit from the opposide side shouldn't trigger`() {
            // arrange
            /**
             *          N
             *   | 1 | 1 | 0 | <-
             *   |-----------|
             * ->| 4 | 0 | 0 |
             *          S
             *  South Kalaha: 7
             */
            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(4, 0, 0),
                northernPits = mutableListOf(0, 1, 1)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 0)

            // assert
            /**
             * Expected
             *          N
             *   | 1 | 1 | 1 | <-
             *   |-----------|
             * ->| 0 | 1 | 1 |
             *          S
             *  South Kalaha: 1
             */
            assertThat(game.table.getPits(Side.SOUTH)).containsExactly(0, 1, 1)
            assertThat(game.table.getPits(Side.NORTH)).containsExactly(1, 1, 1)
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer.opposite())
        }
    }

    @Nested
    inner class LastStoneIntoKalaha {
        @Test
        fun `last stone into own kalaha`() {
            // arrange
            /**
             *       N
             *   | 2 | 2 | <-
             *   |-------|
             * ->| 2 | 2 |
             *       S
             */
            val game: KalahaGame = initialGame(
                southernPits = mutableListOf(2, 2),
                northernPits = mutableListOf(2, 2)
            )
            val initialPlayer: Side = game.currentSide

            // act
            movementManager.move(game, 0)

            // assert
            /**
             *       N
             *   | 2 | 2 | <-
             *   |-------|
             * ->| 0 | 3 |
             *       S
             *  South Kalaha: 7
             */
            assertThat(game.table.getPits(Side.SOUTH)).containsExactly(0, 3)
            assertThat(game.table.getPits(Side.NORTH)).containsExactly(2, 2)
            assertThat(game.table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(game.table.getKalaha(Side.NORTH)).isEqualTo(0)
            assertThat(game.currentSide).isEqualTo(initialPlayer)
        }
    }

    private fun initialGame(
        southernPits: MutableList<Int>,
        northernPits: MutableList<Int>
    ): KalahaGame {
        val table: Table = TableBuilder.sampleTable(
            southernPits = southernPits,
            northernPits = northernPits,
            southernKalaha = 0,
            northernKalaha = 0
        )
        return KalahaGameBuilder.sampleKalahaGame(table = table, currentPlayer = Side.SOUTH)
    }
}