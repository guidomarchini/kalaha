package com.bol.gmarchini.kalaha.model

import kotlin.random.Random

object TableBuilder {
    fun sampleTable(
        id: Int = Random.nextInt(),
        southernPits: MutableList<Int> = mutableListOf(),
        northernPits: MutableList<Int> = mutableListOf(),
        southernKalaha: Int = 0,
        northernKalaha: Int = 0
    ): Table =
        Table(id, southernPits, northernPits, southernKalaha, northernKalaha)
}
