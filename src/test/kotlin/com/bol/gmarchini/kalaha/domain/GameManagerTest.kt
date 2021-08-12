package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.*
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameManagerTest {

    private val movementManagerMock: MovementManager = mock()
    private val gameOverManagerMock: GameOverManager = mock()
    private val gameManager: GameManager = GameManager(
        movementManager = movementManagerMock,
        gameOverManager = gameOverManagerMock
    )

    @AfterEach
    fun afterEach() {
        reset(movementManagerMock, gameOverManagerMock)
    }

    @Nested
    inner class GameOver {
        private val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame()

        fun testCase(isGameOver: Boolean) {
            // arrange
            whenever(gameOverManagerMock.isGameOver(game)).thenReturn(isGameOver)

            // act
            val gameOver: Boolean = gameManager.isGameOver(game)

            // assert
            assertThat(gameOver).isEqualTo(gameOver)
            verify(gameOverManagerMock).isGameOver(game)
        }

        @Test
        fun `calls game over manager - return true`() {
            testCase(true)
        }

        @Test
        fun `calls game over manager - return false`() {
            testCase(false)
        }
    }

    @Nested
    inner class GetWinner {
        private val currentPlayer: Side = Side.SOUTH

        @Test
        fun `winner is SOUTH`() {
            // arrange
            val table: Table = TableBuilder.sampleTable(
                southernKalaha = 1,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(table = table)


            // act
            val winner: Winner = gameManager.getWinner(game)

            // assert
            assertThat(winner).isEqualTo(Winner.SOUTH)
        }

        @Test
        fun `winner is NORTH`() {
            // arrange
            val table: Table = TableBuilder.sampleTable(
                southernKalaha = 0,
                northernKalaha = 1
            )
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(table = table)


            // act
            val winner: Winner = gameManager.getWinner(game)

            // assert
            assertThat(winner).isEqualTo(Winner.NORTH)
        }

        @Test
        fun `tied game`() {
            // arrange
            val table: Table = TableBuilder.sampleTable(
                southernKalaha = 1,
                northernKalaha = 1
            )
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame(table = table)


            // act
            val winner: Winner = gameManager.getWinner(game)

            // assert
            assertThat(winner).isEqualTo(Winner.TIED)
        }
    }

    @Nested
    inner class Moves {

        @Test
        fun `orchestrates the movement`() {
            // arrange
            val game: KalahaGame = KalahaGameBuilder.sampleKalahaGame()
            val pitPosition: Int = 0

            // act
            gameManager.move(game,  pitPosition)

            // assert
            verify(movementManagerMock).move(game, pitPosition)
            verify(gameOverManagerMock).checkForGameOver(game)
        }
    }
}