package com.bol.gmarchini.kalaha.service.mappers

import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.entity.KalahaGameEntity
import com.bol.gmarchini.kalaha.utils.KalahaGameBuilder
import com.bol.gmarchini.kalaha.utils.KalahaGameEntityBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class KalahaGameMapperTest {
    private val kalahaGameMapper: KalahaGameMapper = KalahaGameMapper()

    @Test
    fun `transforms a Kalaha Game into its entity representation`() {
        // arrange
        val kalahaGame: KalahaGame = KalahaGameBuilder.sampleKalahaGame()

        // act
        val kalahaGameEntity: KalahaGameEntity = kalahaGameMapper.toEntity(kalahaGame)

        // assert
        assertThat(kalahaGameEntity.id).isEqualTo(kalahaGame.id)
        assertThat(kalahaGameEntity.currentSide).isEqualTo(kalahaGame.currentSide)
        assertThat(kalahaGameEntity.southernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.SOUTH))
        assertThat(kalahaGameEntity.southernPits.toList()).isEqualTo(kalahaGame.table.getPits(Side.SOUTH))
        assertThat(kalahaGameEntity.northernKalaha).isEqualTo(kalahaGame.table.getKalaha(Side.NORTH))
        assertThat(kalahaGameEntity.northernPits.toList()).isEqualTo(kalahaGame.table.getPits(Side.NORTH))
    }

    @Test
    fun `transforms a Kalaha Game Entity into its domain representation`() {
        // arrange
        val kalahaGameEntity: KalahaGameEntity = KalahaGameEntityBuilder.sampleKalahaGameEntity()

        // act
        val kalahaGame: KalahaGame = kalahaGameMapper.toDomain(kalahaGameEntity)

        // assert
        assertThat(kalahaGame.id).isEqualTo(kalahaGameEntity.id)
        assertThat(kalahaGame.currentSide).isEqualTo(kalahaGameEntity.currentSide)
        assertThat(kalahaGame.table.getKalaha(Side.SOUTH)).isEqualTo(kalahaGameEntity.southernKalaha)
        assertThat(kalahaGame.table.getPits(Side.SOUTH)).isEqualTo(kalahaGameEntity.southernPits.toList())
        assertThat(kalahaGame.table.getKalaha(Side.NORTH)).isEqualTo(kalahaGameEntity.northernKalaha)
        assertThat(kalahaGame.table.getPits(Side.NORTH)).isEqualTo(kalahaGameEntity.northernPits.toList())
    }
}