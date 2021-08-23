package com.bol.gmarchini.kalaha.service

import com.bol.gmarchini.kalaha.domain.GameManager
import com.bol.gmarchini.kalaha.domain.exceptions.InvalidMovementException
import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.KalahaGameRepository
import com.bol.gmarchini.kalaha.persistence.entity.KalahaGameEntity
import com.bol.gmarchini.kalaha.service.exceptions.GameNotFoundException
import com.bol.gmarchini.kalaha.service.mappers.KalahaGameMapper
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import java.util.*

internal class KalahaGameServiceTest {
    private val mapperMock: KalahaGameMapper = mock()
    private val repositoryMock: KalahaGameRepository = mock()
    private val gameManagerMock: GameManager = mock()
    private val pitSize: Int = 6
    private val initialStones: Int = 6
    private val kalahaGameService: KalahaGameService = KalahaGameService(
        repository = repositoryMock,
        mapper = mapperMock,
        gameManager = gameManagerMock,
        pitSize,
        initialStones
    )

    private val gameMock: KalahaGame = mock()
    private val gameEntityMock: KalahaGameEntity = mock()

    @AfterEach
    fun afterEach() {
        reset(mapperMock, repositoryMock, gameMock, gameEntityMock)
    }

    @Nested
    inner class Create {
        @Test
        fun `creates a new Kalaha Game`() {
            // arrange
            whenever(mapperMock.toEntity(any<KalahaGame>())).thenReturn(gameEntityMock)
            whenever(repositoryMock.save(gameEntityMock)).thenReturn(gameEntityMock)
            whenever(mapperMock.toDomain(gameEntityMock)).thenReturn(gameMock)
            val southernPlayer: String = "player1"
            val northernPlayer: String = "player2"

            // act
            val createdGame: KalahaGame = kalahaGameService.create(southernPlayer, northernPlayer)

            // assert
            assertThat(createdGame).isEqualTo(gameMock)

            verify(repositoryMock).save(gameEntityMock)
            val argumentCaptor = argumentCaptor<KalahaGame>()
            verify(mapperMock).toEntity(argumentCaptor.capture())
            val capturedGame: KalahaGame = argumentCaptor.firstValue
            assertThat(capturedGame.table.southernPits).hasSize(pitSize)
            assertThat(capturedGame.table.southernPits).allMatch { it == initialStones }
            assertThat(capturedGame.table.northernPits).hasSize(pitSize)
            assertThat(capturedGame.table.northernPits).allMatch { it == initialStones }
            assertThat(capturedGame.currentSide).isEqualTo(Side.SOUTH)
            assertThat(capturedGame.southernPlayer).isEqualTo(southernPlayer)
            assertThat(capturedGame.northernPlayer).isEqualTo(northernPlayer)
            verify(mapperMock).toDomain(gameEntityMock)
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `gets all Kalaha Game`() {
            // arrange
            whenever(repositoryMock.findAll()).thenReturn(listOf(gameEntityMock))
            whenever(mapperMock.toDomain(gameEntityMock)).thenReturn(gameMock)

            // act
            val allGames: List<KalahaGame> = kalahaGameService.getAll()

            // assert
            assertThat(allGames).containsExactly(gameMock)

            verify(repositoryMock).findAll()
            verify(mapperMock).toDomain(gameEntityMock)
        }
    }

    @Nested
    inner class GetById {
        private val id: Int = 1

        @Test
        fun `no Kalaha Game is found`() {
            // arrange
            whenever(repositoryMock.findById(id)).thenReturn(Optional.empty())

            // act - assert
            assertThrows<GameNotFoundException> {
                kalahaGameService.getById(id)
            }
            verify(repositoryMock).findById(id)
        }

        @Test
        fun `existing Kalaha game`() {
            // arrange
            whenever(repositoryMock.findById(id)).thenReturn(Optional.of(gameEntityMock))
            whenever(mapperMock.toDomain(gameEntityMock)).thenReturn(gameMock)

            // act
            val game: KalahaGame = kalahaGameService.getById(id)

            // assert
            assertThat(game).isEqualTo(gameMock)

            verify(repositoryMock).findById(id)
            verify(mapperMock).toDomain(gameEntityMock)
        }
    }

    @Nested
    inner class Move {
        private val id: Int = 1
        private val pitPosition: Int = 1

        @Test
        fun `no Kalaha Game is found`() {
            // arrange
            whenever(repositoryMock.findById(id)).thenReturn(Optional.empty())

            // act - assert
            assertThrows<GameNotFoundException> {
                kalahaGameService.move(id, pitPosition, "")
            }
            verify(repositoryMock).findById(id)
        }

        @Test
        fun `existing Kalaha game`() {
            // arrange
            whenever(repositoryMock.findById(id)).thenReturn(Optional.of(gameEntityMock))
            whenever(mapperMock.toDomain(gameEntityMock)).thenReturn(gameMock)
            whenever(mapperMock.toEntity(gameMock)).thenReturn(gameEntityMock)
            whenever(repositoryMock.save(gameEntityMock)).thenReturn(gameEntityMock)
            val currentPlayer = "southernPlayer"
            whenever(gameMock.currentPlayer()).thenReturn(currentPlayer)

            // act
            val game: KalahaGame = kalahaGameService.move(id, pitPosition, currentPlayer)

            // assert
            assertThat(game).isEqualTo(gameMock)

            verify(repositoryMock).findById(id)
            verify(gameManagerMock).move(gameMock, pitPosition)
            verify(repositoryMock).save(gameEntityMock)
            verify(mapperMock).toDomain(gameEntityMock)
        }

        @Test
        fun `opponent move`() {
            // arrange
            whenever(repositoryMock.findById(id)).thenReturn(Optional.of(gameEntityMock))
            whenever(mapperMock.toDomain(gameEntityMock)).thenReturn(gameMock)
            val currentPlayer = "northernPlayer"
            whenever(gameMock.currentPlayer()).thenReturn(currentPlayer)

            // act - assert
            assertThrows<InvalidMovementException> {
                kalahaGameService.move(id, pitPosition, "southernPlayer")
            }
        }
    }
}