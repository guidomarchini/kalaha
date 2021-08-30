package gmarchini.kalaha.model

object TableBuilder {
    fun sampleTable(
        southernPits: MutableList<Int> = mutableListOf(),
        northernPits: MutableList<Int> = mutableListOf(),
        southernKalaha: Int = 0,
        northernKalaha: Int = 0
    ): Table =
        Table(southernPits, northernPits, southernKalaha, northernKalaha)
}
