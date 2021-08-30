package gmarchini.kalaha.persistence

import gmarchini.kalaha.persistence.entity.KalahaGameEntity
import gmarchini.kalaha.utils.KalahaGameEntityBuilder
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
        val newInstance: KalahaGameEntity = KalahaGameEntityBuilder.sampleKalahaGameEntity()

        // act
        val savedInstance: KalahaGameEntity = repository.save(newInstance)

        // assert
        assertThat(savedInstance.id).isNotNull
        assertThat(savedInstance.currentSide).isEqualTo(newInstance.currentSide)
        assertThat(savedInstance.southernPits).isEqualTo(newInstance.southernPits)
        assertThat(savedInstance.northernPits).isEqualTo(newInstance.northernPits)
        assertThat(savedInstance.southernKalaha).isEqualTo(newInstance.southernKalaha)
        assertThat(savedInstance.northernKalaha).isEqualTo(newInstance.northernKalaha)
    }

    @Test
    fun `it returns all games of the given player`() {
        // arrange
        val player1: String = "player1"
        val player2: String = "player2"
        val player3: String = "player3"
        val gameAsSouthernPlayer: KalahaGameEntity = entityManager.persistAndFlush(KalahaGameEntityBuilder.sampleKalahaGameEntity(
            southernPlayer = player1,
            northernPlayer = player2
        ))
        val gameAsNorthernPlayer: KalahaGameEntity = entityManager.persistAndFlush(KalahaGameEntityBuilder.sampleKalahaGameEntity(
            southernPlayer = player3,
            northernPlayer = player1
        ))
        entityManager.persistAndFlush(KalahaGameEntityBuilder.sampleKalahaGameEntity(
            southernPlayer = player2,
            northernPlayer = player3
        ))

        // act
        val playedGames = this.repository.getGamesFromPlayer(player1)

        // assert
        assertThat(playedGames).containsExactlyInAnyOrder(gameAsSouthernPlayer, gameAsNorthernPlayer)
    }

    @Test
    fun `it returns a game`() {
        // arrange
        val kalahaGame: KalahaGameEntity = entityManager.persistAndFlush(KalahaGameEntityBuilder.sampleKalahaGameEntity())

        // act
        val found = repository.findByIdOrNull(kalahaGame.id!!)

        // assert
        assertThat(found).isEqualTo(kalahaGame)
    }

    @Test
    fun `it updates a game`() {
        // arrange
        val kalahaGame: KalahaGameEntity = entityManager.persistAndFlush(
            KalahaGameEntityBuilder.sampleKalahaGameEntity(
                southernPits = intArrayOf(4, 4, 4),
                northernPits = intArrayOf(4, 4, 4),
                southernKalaha = 0,
                northernKalaha = 0
            )
        )
        val toUpdate: KalahaGameEntity = KalahaGameEntityBuilder.sampleKalahaGameEntity(
            id = kalahaGame.id,
            southernPits = intArrayOf(4, 0, 5),
            northernPits = intArrayOf(3, 3, 2),
            southernKalaha = 1,
            northernKalaha = 0
        )

        // act
        val updatedGame = repository.save(toUpdate)

        // assert
        assertThat(updatedGame.southernPits).isEqualTo(toUpdate.southernPits)
        assertThat(updatedGame.northernPits).isEqualTo(toUpdate.northernPits)
        assertThat(updatedGame.southernKalaha).isEqualTo(toUpdate.southernKalaha)
        assertThat(updatedGame.northernKalaha).isEqualTo(toUpdate.northernKalaha)
    }

    @Test
    fun `it removes a game`() {
        // arrange
        val kalahaGame: KalahaGameEntity = entityManager.persistAndFlush(KalahaGameEntityBuilder.sampleKalahaGameEntity())
        val entityId: Int = kalahaGame.id!!

        // act
        repository.deleteById(entityId)

        // assert
        assertThat(repository.findByIdOrNull(entityId)).isNull()
    }
}