package gmarchini.kalaha.model

/**
 * The Kalaha table.
 * It contains two sides: South and North.
 * Each side has a Kalaha and a given number of pits.
 * We could say that the Kalaha is the current amount of points the player has.
 * Each pit can have any amount of stones.
 */
data class Table (
    val southernPits: MutableList<Int>,
    val northernPits: MutableList<Int>,
    var southernKalaha: Int,
    var northernKalaha: Int
) {
    fun getPits(side: Side): MutableList<Int> = when(side) {
        Side.SOUTH -> southernPits
        Side.NORTH -> northernPits
    }

    fun getKalaha(side: Side): Int = when(side) {
        Side.SOUTH -> southernKalaha
        Side.NORTH -> northernKalaha
    }

    /**
     * Adds the given stones to the given Kalaha. 1 stone by default.
     */
    fun addStonesToKalaha(side: Side, stoneQuantity: Int = 1): Unit = when(side) {
        Side.SOUTH -> this.southernKalaha += stoneQuantity
        Side.NORTH -> this.northernKalaha += stoneQuantity
    }
}