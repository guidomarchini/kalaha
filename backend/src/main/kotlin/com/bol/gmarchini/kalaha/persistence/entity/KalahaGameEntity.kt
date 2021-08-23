package com.bol.gmarchini.kalaha.persistence.entity

import com.bol.gmarchini.kalaha.model.Side
import java.util.*
import javax.persistence.*

/**
 * Game representation in the database. A specific game has only one table
 */
@Entity
data class KalahaGameEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Enumerated(EnumType.STRING)
    val currentSide: Side,
    val southernPlayer: String,
    val northernPlayer: String,

    val ended: Boolean,
    val createdDate: Date,

    // table properties
    val southernPits: IntArray,
    val northernPits: IntArray,
    val southernKalaha: Int,
    val northernKalaha: Int
)