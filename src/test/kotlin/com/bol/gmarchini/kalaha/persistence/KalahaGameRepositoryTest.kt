package com.bol.gmarchini.kalaha.persistence

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.entity.KalahaGameEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
internal class KalahaGameRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val repository: KalahaGameRepository
) {
    @AfterEach
    fun afterEach() {
        repository.deleteAll()
    }

    @Test
    fun `it saves a game`() {
        // arrange
        val newInstance: KalahaGameEntity = sampleEntity()

        // act
        val savedInstance: KalahaGameEntity = repository.save(newInstance)

        // assert
        assertThat(savedInstance.id).isNotNull
        assertThat(savedInstance.currentPlayer).isEqualTo(newInstance.currentPlayer)
        assertThat(savedInstance.southernPits).isEqualTo(newInstance.southernPits)
        assertThat(savedInstance.northernPits).isEqualTo(newInstance.northernPits)
        assertThat(savedInstance.southernKalaha).isEqualTo(newInstance.southernKalaha)
        assertThat(savedInstance.northernKalaha).isEqualTo(newInstance.northernKalaha)
    }

    @Test
    fun `it returns a game`() {
        // arrange
        val kalahaGame: KalahaGameEntity = entityManager.persistAndFlush(sampleEntity())

        // act
        val found = repository.findByIdOrNull(kalahaGame.id!!)

        // assert
        assertThat(found).isEqualTo(kalahaGame)
    }

    @Test
    fun `it removes a game`() {
        // arrange
        val kalahaGame: KalahaGameEntity = entityManager.persistAndFlush(sampleEntity())
        val entityId: Int = kalahaGame.id!!

        // act
        repository.deleteById(entityId)

        // assert
        assertThat(repository.findByIdOrNull(entityId)).isNull()
    }

    fun sampleEntity(): KalahaGameEntity =
        KalahaGameEntity(
            currentPlayer = Side.SOUTH,
            southernPits = intArrayOf(1),
            northernPits = intArrayOf(1),
            southernKalaha = 0,
            northernKalaha = 0
        )
}