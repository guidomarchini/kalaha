package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.domain.exceptions.InvalidMovementException
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
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
            val table: Table = Table.new(1, 0)

            // act - assert
            assertThrows<InvalidMovementException> {
                movementManager.move(table, pitPosition = 0, playerSide)
            }
        }
    }

    @Nested
    inner class SpecialMovement {
        /**
         * Table would be like
         *          N
         *   | 1 | 6 | 1 | 0 | <-
         *   |---------------|
         * ->| 1 | 0 | 1 | 0 |
         *          S
         *  South Kalaha: 0
         *  North Kalaha: 0
         */
        private fun initialTable(): Table = Table.restore(
            southernPits = mutableListOf(1, 0, 1, 0),
            northernPits = mutableListOf(0, 1, 6, 1),
            southernKalaha = 0,
            northernKalaha = 0
        )

        @Test
        fun `move with stones on the opposite side`() {
            // arrange
            val playerSide: Side = Side.SOUTH
            val table: Table = initialTable()

            // act
            movementManager.move(table, 0, playerSide)

            // assert
            /**
             * Expected
             *          N
             *   | 1 | 0 | 1 | 0 | <-
             *   |---------------|
             * ->| 0 | 0 | 1 | 0 |
             *          S
             *  South Kalaha: 7
             *  North Kalaha: 0
             */
            assertThat(table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(0, 0, 1, 0))
            assertThat(table.getPits(Side.NORTH)).isEqualTo(mutableListOf(0, 1, 0, 1))
            assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(7)
            assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)
        }

        @Test
        fun `move without stones on the opposite side`() {
            // arrange
            val playerSide: Side = Side.SOUTH
            val table: Table = initialTable()

            // act
            movementManager.move(table, 2, playerSide)

            // assert
            /**
             * Expected
             *          N
             *   | 1 | 6 | 1 | 0 | <-
             *   |---------------|
             * ->| 1 | 0 | 0 | 0 |
             *          S
             *  South Kalaha: 1
             *  North Kalaha: 0
             */
            assertThat(table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(1, 0, 0, 0))
            assertThat(table.getPits(Side.NORTH)).isEqualTo(mutableListOf(0, 1, 6, 1))
            assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)
        }
    }

    @Nested
    inner class NormalMove {
        /**
         * Table would be like
         *          N
         *   | 6 | 6 | 6 | 6 | <-
         *   |---------------|
         * ->| 2 | 0 | 4 | 8 |
         *          S
         *  South Kalaha: 0
         *  North Kalaha: 0
         */
        private fun initialTable(): Table = Table.restore(
            southernPits = mutableListOf(2, 0, 4, 8),
            northernPits = mutableListOf(6, 6, 6, 6),
            southernKalaha = 0,
            northernKalaha = 0
        )

        @Test
        fun `move inside the pit`() {
            // arrange
            val playerSide: Side = Side.SOUTH
            val table: Table = initialTable()

            // act
            movementManager.move(table, 0, playerSide)

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
            assertThat(table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(0, 1, 5, 8))
            assertThat(table.getPits(Side.NORTH)).isEqualTo(mutableListOf(6, 6, 6, 6))
            assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(0)
            assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)
        }

        @Test
        fun `move cross the kalaha side`() {
            // arrange
            val playerSide: Side = Side.SOUTH
            val table: Table = initialTable()

            // act
            movementManager.move(table, 2, playerSide)

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
            assertThat(table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(2, 0, 0, 9))
            assertThat(table.getPits(Side.NORTH)).isEqualTo(mutableListOf(7, 7, 6, 6))
            assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)
        }

        @Test
        fun `move cross the kalaha side twice`() {
            // arrange
            val playerSide: Side = Side.SOUTH
            val table: Table = initialTable()

            // act
            movementManager.move(table, 3, playerSide)

            // assert
            /**
             * Expected
             *          N
             *   | 7 | 7 | 7 | 7 | <-
             *   |---------------|
             * ->| 3 | 1 | 5 | 0 |
             *          S
             *  South Kalaha: 1
             *  North Kalaha: 0
             */
            assertThat(table.getPits(Side.SOUTH)).isEqualTo(mutableListOf(3, 1, 5, 0))
            assertThat(table.getPits(Side.NORTH)).isEqualTo(mutableListOf(7, 7, 7, 7))
            assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(1)
            assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)
        }
    }
}