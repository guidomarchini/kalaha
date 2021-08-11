package com.bol.gmarchini.kalaha.service

import com.bol.gmarchini.kalaha.application.dto.KalahaGameDto
import com.bol.gmarchini.kalaha.domain.KalahaGame
import com.bol.gmarchini.kalaha.persistence.KalahaGameRepository
import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
import com.bol.gmarchini.kalaha.service.exceptions.GameNotFoundException
import com.bol.gmarchini.kalaha.service.mappers.KalahaGameMapper
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class KalahaGameServiceTest {
    private val mapperMock: KalahaGameMapper = mock()
    private val repositoryMock: KalahaGameRepository = mock()
    private val kalahaGameService: KalahaGameService = KalahaGameService(repository = repositoryMock, mapper = mapperMock)

    private val gameDomainMock: KalahaGame = mock()
    private val gameEntityMock: KalahaGameEntity = mock()
    private val gameDtoMock: KalahaGameDto = mock()

    @AfterEach
    fun afterEach() {
        reset(mapperMock, repositoryMock)
    }

    @Nested
    inner class Create {
        @Test
        fun `creates a new Kalaha Game`() {
            // arrange
            whenever(mapperMock.toEntity(any(), isNull())).thenReturn(gameEntityMock)
            whenever(repositoryMock.save(gameEntityMock)).thenReturn(gameEntityMock)
            whenever(mapperMock.toApplication(gameEntityMock)).thenReturn(gameDtoMock)

            // act
            val createdGame: KalahaGameDto = kalahaGameService.create()

            // assert
            assertThat(createdGame).isEqualTo(gameDtoMock)

            verify(mapperMock).toEntity(any(), isNull())
            verify(repositoryMock).save(gameEntityMock)
            verify(mapperMock).toApplication(gameEntityMock)
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `gets all Kalaha Game`() {
            // arrange
            whenever(repositoryMock.getAllByOrderByEnded()).thenReturn(listOf(gameEntityMock))
            whenever(mapperMock.toApplication(gameEntityMock)).thenReturn(gameDtoMock)

            // act
            val allGames: List<KalahaGameDto> = kalahaGameService.getAll()

            // assert
            assertThat(allGames).containsExactly(gameDtoMock)

            verify(repositoryMock).getAllByOrderByEnded()
            verify(mapperMock).toApplication(gameEntityMock)
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
            whenever(mapperMock.toApplication(gameEntityMock)).thenReturn(gameDtoMock)

            // act
            val game: KalahaGameDto = kalahaGameService.getById(id)

            // assert
            assertThat(game).isEqualTo(gameDtoMock)

            verify(repositoryMock).findById(id)
            verify(mapperMock).toApplication(gameEntityMock)
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
                kalahaGameService.move(id, pitPosition)
            }
            verify(repositoryMock).findById(id)
        }

        @Test
        fun `existing Kalaha game`() {
            // arrange
            whenever(repositoryMock.findById(id)).thenReturn(Optional.of(gameEntityMock))
            whenever(mapperMock.toDomain(gameEntityMock)).thenReturn(gameDomainMock)
            whenever(mapperMock.toEntity(gameDomainMock, id)).thenReturn(gameEntityMock)
            whenever(mapperMock.toApplication(gameEntityMock)).thenReturn(gameDtoMock)
            whenever(repositoryMock.save(gameEntityMock)).thenReturn(gameEntityMock)

            // act
            val game: KalahaGameDto = kalahaGameService.move(id, pitPosition)

            // assert
            assertThat(game).isEqualTo(gameDtoMock)

            verify(repositoryMock).findById(id)
            verify(mapperMock).toDomain(gameEntityMock)
            verify(gameDomainMock).move(pitPosition)
            verify(mapperMock).toEntity(gameDomainMock, id)
            verify(repositoryMock).save(gameEntityMock)
            verify(mapperMock).toApplication(gameEntityMock)
        }
    }
}