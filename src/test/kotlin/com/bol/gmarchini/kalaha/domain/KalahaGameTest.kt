package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class KalahaGameTest {

    private val movementManagerMock: MovementManager = mock()
    private val gameOverManagerMock: GameOverManager = mock()
    private val tableMock: Table = mock()

    @Nested
    inner class Initializations {
        @Test
        fun `default game creation`() {
            // arrange
            val defaultPitSize: Int = 6
            val defaultInitialStones: Int = 6

            // act
            val game: KalahaGame = KalahaGame.new()

            // assert
            assertThat(game.currentPlayer).isEqualTo(Side.SOUTH)
            for (side: Side in Side.values()) {
                assertThat(game.table.pits).containsKey(side)
                assertThat(game.table.getPits(side)).hasSize(defaultPitSize)
                assertThat(game.table.getPits(side)).allMatch { it == defaultInitialStones }

                assertThat(game.table.kalahas).containsKey(side)
                assertThat(game.table.getKalaha(side)).isEqualTo(0)
            }
        }
    }

    @Nested
    inner class GameOver {
        private val currentPlayer: Side = Side.SOUTH
        private val game: KalahaGame = KalahaGame.restore(tableMock, currentPlayer, movementManagerMock, gameOverManagerMock)

        @AfterEach
        fun afterEach() {
            reset(gameOverManagerMock)
        }

        @Test
        fun `delegates the functionality to game over manager`() {
            // arrange
            whenever(gameOverManagerMock.isGameOver(tableMock, currentPlayer)).thenReturn(true)

            // act
            val isGameOver: Boolean = game.isGameOver()

            // assert
            assertThat(isGameOver).isTrue
            verify(gameOverManagerMock).isGameOver(tableMock, currentPlayer)
        }
    }

    @Nested
    inner class Winner {
        private val currentPlayer: Side = Side.SOUTH

        @Test
        fun `winner is SOUTH`() {
            // arrange
            val table: Table = Table.restore(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 1,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGame.restore(table, currentPlayer, movementManagerMock, gameOverManagerMock)


            // act
            val winner: Side? = game.winner()

            // assert
            assertThat(winner).isEqualTo(Side.SOUTH)
        }

        @Test
        fun `winner is NORTH`() {
            // arrange
            val table: Table = Table.restore(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 0,
                northernKalaha = 1
            )
            val game: KalahaGame = KalahaGame.restore(table, currentPlayer, movementManagerMock, gameOverManagerMock)

            // act
            val winner: Side? = game.winner()

            // assert
            assertThat(winner).isEqualTo(Side.NORTH)
        }

        @Test
        fun `tied game`() {
            // arrange
            val table: Table = Table.restore(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 1,
                northernKalaha = 1
            )
            val game: KalahaGame = KalahaGame.restore(table, currentPlayer, movementManagerMock, gameOverManagerMock)

            // act
            val winner: Side? = game.winner()

            // assert
            assertThat(winner).isNull()
        }
    }

    @Nested
    inner class Moves {
        private val initialPlayer: Side = Side.SOUTH

        @AfterEach
        fun afterEach() {
            reset(movementManagerMock, gameOverManagerMock)
        }

        @Test
        fun `orchestrates the movement`() {
            // arrange
            val game: KalahaGame = KalahaGame.restore(tableMock, initialPlayer, movementManagerMock, gameOverManagerMock)
            val pitPosition: Int = 0

            // act
            game.move(pitPosition)

            // assert
            verify(movementManagerMock).move(tableMock, pitPosition, initialPlayer)
            verify(gameOverManagerMock).checkForGameOver(tableMock, initialPlayer.opposite())
            assertThat(game.currentPlayer).isEqualTo(initialPlayer.opposite())
            // game is not over yet
            verifyZeroInteractions(tableMock)
        }
    }
}