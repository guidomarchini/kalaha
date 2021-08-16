package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.utils.KalahaGameBuilder
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
            val table: Table = Table(
                southernPits = mutableListOf(0, 0, 0, 0),
                northernPits = mutableListOf(0, 1, 6, 1),
                southernKalaha = 0,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(currentPlayer = currentPlayer, table = table)

            // act
            val isGameOver: Boolean = gameOverManager.isGameOver(game)

            // assert
            assertThat(isGameOver).isTrue
        }

        @Test
        fun `game is not over`() {
            // arrange
            val currentPlayer: Side = Side.NORTH
            val table: Table = Table(
                southernPits = mutableListOf(0, 0, 0, 0),
                northernPits = mutableListOf(0, 1, 6, 1),
                southernKalaha = 0,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(currentPlayer = currentPlayer, table = table)

            // act
            val isGameOver: Boolean = gameOverManager.isGameOver(game)

            // assert
            assertThat(isGameOver).isFalse
        }
    }

    @Nested
    inner class GameOverCheck {
        fun `leaves the table as it if game is not over`() {
            // arrange
            val table: Table = Table(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 0,
                northernKalaha = 0
            )
            val currentPlayer: Side = Side.SOUTH
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(currentPlayer = currentPlayer, table = table)

            // act
            gameOverManager.checkForGameOver(game)

            // assert
            assertThat(table.getPits(currentPlayer)).isEqualTo(mutableListOf(1))
            assertThat(table.getPits(currentPlayer.opposite())).isEqualTo(mutableListOf(1))
            assertThat(table.getKalaha(currentPlayer)).isEqualTo(0)
            assertThat(table.getKalaha(currentPlayer.opposite())).isEqualTo(0)
        }
    }

    fun `handles a game over`() {
        // arrange
        val table: Table = Table(
            southernPits = mutableListOf(2, 0, 0),
            northernPits = mutableListOf(0, 0, 0), // next player has no moves
            southernKalaha = 0,
            northernKalaha = 0
        )
        val currentPlayer: Side = Side.SOUTH
        val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(currentPlayer = currentPlayer, table = table)

        // act
        gameOverManager.checkForGameOver(game)

        // assert
        assertThat(table.getPits(currentPlayer)).isEqualTo(mutableListOf(0, 0, 0))
        assertThat(table.getPits(currentPlayer.opposite())).isEqualTo(mutableListOf(0, 0, 0))
        assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(2)
        assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)
    }
}