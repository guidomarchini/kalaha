package com.bol.gmarchini.kalaha.service.mappers

import com.bol.gmarchini.kalaha.domain.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
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
}