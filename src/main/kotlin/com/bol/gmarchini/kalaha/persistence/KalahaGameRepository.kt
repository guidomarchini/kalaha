package com.bol.gmarchini.kalaha.persistence

import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
import org.springframework.data.repository.CrudRepository

/**
 * The Kalaha game repository.
 */
interface KalahaGameRepository: CrudRepository<KalahaGameEntity, Int> {
    fun getAllByOrderByEnded(): Iterable<KalahaGameEntity>
}