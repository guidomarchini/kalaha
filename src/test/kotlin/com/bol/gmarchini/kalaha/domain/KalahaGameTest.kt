package com.bol.gmarchini.kalaha.domain

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class KalahaGameTest {

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
            assertThat(game.movementManager).isNotNull
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
        private val movementManager: MovementManager = mock()

        @Test
        fun `game is over`() {
            // arrange
            val currentPlayer: Side = Side.SOUTH
            val table: Table = Table.customized(
                southernPits = mutableListOf(0, 0, 0, 0),
                northernPits = mutableListOf(0, 1, 6, 1),
                southernKalaha = 0,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGame.customizedGame(table, currentPlayer, movementManager)

            // act
            val isGameOver: Boolean = game.isGameOver()

            // assert
            assertThat(isGameOver).isTrue
        }

        @Test
        fun `game is not over`() {
            // arrange
            val currentPlayer: Side = Side.NORTH
            val table: Table = Table.customized(
                southernPits = mutableListOf(0, 0, 0, 0),
                northernPits = mutableListOf(0, 1, 6, 1),
                southernKalaha = 0,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGame.customizedGame(table, currentPlayer, movementManager)

            // act
            val isGameOver: Boolean = game.isGameOver()

            // assert
            assertThat(isGameOver).isFalse
        }
    }

    @Nested
    inner class Winner {
        private val movementManager: MovementManager = mock()
        private val currentPlayer: Side = Side.SOUTH

        @Test
        fun `winner is SOUTH`() {
            // arrange
            val table: Table = Table.customized(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 1,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGame.customizedGame(table, currentPlayer, movementManager)


            // act
            val winner: Side? = game.winner()

            // assert
            assertThat(winner).isEqualTo(Side.SOUTH)
        }

        @Test
        fun `winner is NORTH`() {
            // arrange
            val table: Table = Table.customized(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 0,
                northernKalaha = 1
            )
            val game: KalahaGame = KalahaGame.customizedGame(table, currentPlayer, movementManager)

            // act
            val winner: Side? = game.winner()

            // assert
            assertThat(winner).isEqualTo(Side.NORTH)
        }

        @Test
        fun `tied game`() {
            // arrange
            val table: Table = Table.customized(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 1,
                northernKalaha = 1
            )
            val game: KalahaGame = KalahaGame.customizedGame(table, currentPlayer, movementManager)

            // act
            val winner: Side? = game.winner()

            // assert
            assertThat(winner).isNull()
        }
    }

    @Nested
    inner class Moves {
        private val movementManager: MovementManager = mock()
        private val initialPlayer: Side = Side.SOUTH

        @AfterEach
        fun afterEach() {
            reset(movementManager)
        }

        @Test
        fun `Movement switches player`() {
            // arrange
            val table: Table = Table.customized(
                southernPits = mutableListOf(1),
                northernPits = mutableListOf(1),
                southernKalaha = 0,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGame.customizedGame(table, initialPlayer, movementManager)
            val pitPosition: Int = 0

            // act
            game.move(pitPosition)

            // assert
            verify(movementManager).move(pitPosition, initialPlayer)
            assertThat(game.currentPlayer).isEqualTo(initialPlayer.opposite())
            // game is not over yet
            assertThat(table.getPits(initialPlayer)).isEqualTo(mutableListOf(1))
            assertThat(table.getPits(initialPlayer.opposite())).isEqualTo(mutableListOf(1))
            assertThat(table.getKalaha(initialPlayer)).isEqualTo(0)
            assertThat(table.getKalaha(initialPlayer.opposite())).isEqualTo(0)
        }

        @Test
        fun `Movement with game over`() {
            // arrange
            val table: Table = Table.customized(
                southernPits = mutableListOf(2, 0, 0),
                northernPits = mutableListOf(0, 0, 0), // next player has no moves
                southernKalaha = 0,
                northernKalaha = 0
            )
            val game: KalahaGame = KalahaGame.customizedGame(table, initialPlayer, movementManager)
            val pitPosition: Int = 0

            // act
            game.move(pitPosition)

            // assert
            verify(movementManager).move(pitPosition, initialPlayer)
            assertThat(game.currentPlayer).isEqualTo(initialPlayer.opposite())

            // game over
            assertThat(table.getPits(initialPlayer)).isEqualTo(mutableListOf(0, 0, 0))
            assertThat(table.getPits(initialPlayer.opposite())).isEqualTo(mutableListOf(0, 0, 0))
            assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(2)
            assertThat(table.getKalaha(Side.NORTH)).isEqualTo(0)

            assertThat(game.isGameOver()).isTrue
            assertThat(game.winner()).isEqualTo(Side.SOUTH)
        }
    }
}