package gmarchini.kalaha.persistence

import gmarchini.kalaha.persistence.entity.KalahaGameEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

/**
 * The Kalaha game repository.
 */
interface KalahaGameRepository: CrudRepository<KalahaGameEntity, Int> {
    @Query("SELECT game FROM KalahaGameEntity game WHERE game.southernPlayer = ?1 OR game.northernPlayer = ?1")
    fun getGamesFromPlayer(playerName: String): Iterable<KalahaGameEntity>
}