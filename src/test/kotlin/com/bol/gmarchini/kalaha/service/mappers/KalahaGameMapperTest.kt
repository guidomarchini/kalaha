package com.bol.gmarchini.kalaha.service.mappers

import com.bol.gmarchini.kalaha.application.dto.KalahaGameDto
import com.bol.gmarchini.kalaha.domain.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Winner
import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class KalahaGameMapperTest {
    private val kalahaGameMapper: KalahaGameMapper = KalahaGameMapper()

    @Test
    fun `transforms a Kalaha Game into entity`() {
        // arrange
        val kalahaGame: KalahaGame = KalahaGame.new()

        // act
        val entity: KalahaGameEntity = kalahaGameMapper.toEntity(kalahaGame, null)

        // assert
        assertThat(entity.id).isNull()
        assertThat(entity.currentPlayer).isEqualTo(kalahaGame.currentPlayer)
        assertThat(entity.ended).isEqualTo(kalahaGame.isGameOver())
        assertThat(entity.southernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.SOUTH))
        assertThat(entity.southernPits).isEqualTo(kalahaGame.table.getPits(Side.SOUTH).toIntArray())
        assertThat(entity.northernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.NORTH))
        assertThat(entity.northernPits).isEqualTo(kalahaGame.table.getPits(Side.NORTH).toIntArray())
    }

    @Test
    fun `transforms an existent Kalaha Game into entity`() {
        // arrange
        val kalahaGame: KalahaGame = KalahaGame.new()
        val id: Int = 1

        // act
        val kalahaGameEntity: KalahaGameEntity = kalahaGameMapper.toEntity(kalahaGame, id)

        // assert
        assertThat(kalahaGameEntity.id).isEqualTo(id)
        assertThat(kalahaGameEntity.currentPlayer).isEqualTo(kalahaGame.currentPlayer)
        assertThat(kalahaGameEntity.ended).isEqualTo(kalahaGame.isGameOver())
        assertThat(kalahaGameEntity.southernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.SOUTH))
        assertThat(kalahaGameEntity.southernPits).isEqualTo(kalahaGame.table.getPits(Side.SOUTH).toIntArray())
        assertThat(kalahaGameEntity.northernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.NORTH))
        assertThat(kalahaGameEntity.northernPits).isEqualTo(kalahaGame.table.getPits(Side.NORTH).toIntArray())
    }

    @Test
    fun `transforms a Kalaha Game entity into a domain Kalaha Game`() {
        // arrange
        val kalahaGameEntity: KalahaGameEntity = KalahaGameEntity(
            currentPlayer = Side.SOUTH,
            southernPits = intArrayOf(1, 2, 3),
            northernPits = intArrayOf(3, 2, 1),
            southernKalaha = 0,
            northernKalaha = 1,
            ended = true
        )

        // act
        val kalahaGame: KalahaGame = kalahaGameMapper.toDomain(kalahaGameEntity)

        // assert
        assertThat(kalahaGame.currentPlayer).isEqualTo(kalahaGameEntity.currentPlayer)
        assertThat(kalahaGame.table.getKalaha(Side.SOUTH)).isEqualTo(kalahaGameEntity.southernKalaha)
        assertThat(kalahaGame.table.getPits(Side.SOUTH)).isEqualTo(kalahaGameEntity.southernPits.toMutableList())
        assertThat(kalahaGame.table.getKalaha(Side.NORTH)).isEqualTo(kalahaGameEntity.northernKalaha)
        assertThat(kalahaGame.table.getPits(Side.NORTH)).isEqualTo(kalahaGameEntity.northernPits.toMutableList())
    }

    @Test
    fun `transforms a domain Kalaha Game into a DTO`() {
        // arrange
        val kalahaGame: KalahaGame = KalahaGame.new()
        val id: Int = 1

        // act
        val kalahaGameDto: KalahaGameDto = kalahaGameMapper.toApplication(kalahaGame, id)

        // assert
        assertThat(kalahaGameDto.id).isEqualTo(id)
        assertThat(kalahaGameDto.currentPlayer).isEqualTo(kalahaGame.currentPlayer)
        assertThat(kalahaGameDto.southernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.SOUTH))
        assertThat(kalahaGameDto.southernPits).isEqualTo(kalahaGame.table.getPits(Side.SOUTH))
        assertThat(kalahaGameDto.northernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.NORTH))
        assertThat(kalahaGameDto.northernPits).isEqualTo(kalahaGame.table.getPits(Side.NORTH))
        assertThat(kalahaGameDto.ended).isEqualTo(kalahaGame.isGameOver())
        assertThat(kalahaGameDto.winner).isNull()
    }

    @Test
    fun `transforms an ended domain Kalaha Game into a DTO`() {
        // arrange
        val kalahaGame: KalahaGame = spy(KalahaGame.new())
        whenever(kalahaGame.isGameOver()).thenReturn(true)
        val tied = Winner.TIED
        whenever(kalahaGame.getWinner()).thenReturn(tied)
        val id: Int = 1

        // act
        val kalahaGameDto: KalahaGameDto = kalahaGameMapper.toApplication(kalahaGame, id)

        // assert
        assertThat(kalahaGameDto.id).isEqualTo(id)
        assertThat(kalahaGameDto.currentPlayer).isEqualTo(kalahaGame.currentPlayer)
        assertThat(kalahaGameDto.southernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.SOUTH))
        assertThat(kalahaGameDto.southernPits).isEqualTo(kalahaGame.table.getPits(Side.SOUTH))
        assertThat(kalahaGameDto.northernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.NORTH))
        assertThat(kalahaGameDto.northernPits).isEqualTo(kalahaGame.table.getPits(Side.NORTH))
        assertThat(kalahaGameDto.ended).isEqualTo(true)
        assertThat(kalahaGameDto.winner).isEqualTo(tied)
    }
}