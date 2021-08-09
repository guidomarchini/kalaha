package com.bol.gmarchini.kalaha.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class TableTest {
    @Test
    fun `create a new Table contains the given parameters`() {
        // arrange
        val pitSize = 6
        val initialStones = 6

        // act
        val table: Table = Table.new(pitSize, initialStones)

        // assert
        // sides are symmetric
        for (side: Side in Side.values()) {
            assertThat(table.pits).containsKey(side)
            assertThat(table.getPits(side)).hasSize(pitSize)
            assertThat(table.getPits(side)).allMatch { it == initialStones }

            assertThat(table.kalahas).containsKey(side)
            assertThat(table.getKalaha(side)).isEqualTo(0)
        }
    }

    @Test
    fun `custom creation`() {
        // arrange
        val southernPits: MutableList<Int> = mutableListOf(1)
        val northernPits: MutableList<Int> = mutableListOf(0)
        val southernKalaha: Int = 1
        val northernKalaha: Int = 0

        // act
        val table: Table = Table.customized(
            southernPits, northernPits, southernKalaha, northernKalaha
        )

        // assert
        assertThat(table.getPits(Side.SOUTH)).isEqualTo(southernPits)
        assertThat(table.getPits(Side.NORTH)).isEqualTo(northernPits)
        assertThat(table.getKalaha(Side.SOUTH)).isEqualTo(southernKalaha)
        assertThat(table.getKalaha(Side.NORTH)).isEqualTo(northernKalaha)
    }
}