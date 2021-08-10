package com.bol.gmarchini.kalaha.persistence.entities

import com.bol.gmarchini.kalaha.model.Side
import javax.persistence.*

/**
 * Game representation in the database. A specific game has only one table
 */
@Entity
data class KalahaGameEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Enumerated(EnumType.STRING)
    val currentPlayer: Side,

    // table properties
    val southernPits: IntArray,
    val northernPits: IntArray,
    val southernKalaha: Int,
    val northernKalaha: Int,

    // additional properties, used for sorting
    val ended: Boolean

)