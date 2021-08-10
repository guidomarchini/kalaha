package com.bol.gmarchini.kalaha.persistence

import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
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
        assertThat(savedInstance.ended).isEqualTo(newInstance.ended)
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

    @Test
    fun `it returns the ongoing games first`() {
        // arrange
        val endedKalahaGame: KalahaGameEntity = entityManager.persistAndFlush(
            sampleEntity(ended = true)
        )
        val ongoingKalahaGame: KalahaGameEntity = entityManager.persistAndFlush(
            sampleEntity(ended = false)
        )

        // act
        val found = repository.getAllByOrderByEnded()

        // assert
        assertThat(found).isNotEmpty
        assertThat(found).hasSize(2)
        assertThat(found).containsExactly(ongoingKalahaGame, endedKalahaGame)
    }


    fun sampleEntity(ended: Boolean = false): KalahaGameEntity =
        KalahaGameEntity(
            currentPlayer = Side.SOUTH,
            southernPits = intArrayOf(),
            northernPits = intArrayOf(),
            southernKalaha = 0,
            northernKalaha = 0,
            ended = ended
        )
}