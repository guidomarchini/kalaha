package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameOverManagerTest {
    private val gameOverManager: GameOverManager = GameOverManager()

    @Nested
    inner class IsGameOver {
        fun `game is over`() {
            // arrange
            val currentPlayer: Side = Side.SOUTH
            val table: Table = Table.restore(
                southernPits = mutableListOf(0, 0, 0, 0),
                northernPits = mutableListOf(0, 1, 6, 1),
                southernKalaha = 0,
                northernKalaha = 0
            )

            // act
            val isGameOver: Boolean = gameOverManager.isGameOver(table, currentPlayer)

            // assert
            assertThat(isGameOver).isTrue
        }

        @Test
        fun `game is not over`() {
            // arrange
            val currentPlayer: Side = Side.NORTH
            val table: Table = Table.restore(
                southernPits = mutableListOf(0, 0, 0, 0),
                northernPits = mutableListOf(0, 1, 6, 1),
                southernKalaha = 0,
                northernKalaha = 0
            )

            // act
            val isGameOver: Boolean = gameOverManager.isGameOver(table, currentPlayer)

            // assert
            assertThat(isGameOver).isFalse
        }
    }

    @Nested
    inner class GameOverCheck {
        fun `leaves the table as it if game is not over`() {
            // arrange
            val table: Table = Table.restore(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 0,
                northernKalaha = 0
            )
            val currentSide: Side = Side.SOUTH

            // act
            gameOverManager.checkForGameOver(table, currentSide)

            // assert
            assertThat(table.getPits(currentSide)).isEqualTo(mutableListOf(1))
            assertThat(table.getPits(currentSide.opposite())).isEqualTo(mutableListOf(1))
            assertThat(table.getKalaha(currentSide)).isEqualTo(0)
            assertThat(table.getKalaha(currentSide.opposite())).isEqualTo(0)
        }
    }

    fun `handles a game over`() {
        // arrange
        val table: Table = Table.restore(
            southernPits = mutableListOf(2, 0, 0),
            northernPits = mutableListOf(0, 0, 0), // next player has no moves
            southernKalaha = 0,
            northernKalaha = 0
        )
        val currentSide: Side = Side.SOUTH

        // act
        gameOverManager.checkForGameOver(table, currentSide)

        // assert
        assertThat(table.getPits(currentSide)).isEqualTo(mutableListOf(0, 0, 0))
        assertThat(table.getPits(currentSide.opposite())).isEqualTo(mutableListOf(0, 0, 0))
        assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(2)
        assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)
    }
}