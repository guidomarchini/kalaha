package gmarchini.kalaha.model

/**
 * A side of the gmarchini.kalaha.
 * There are two sides: North Kalaha and South Kalaha.
 */
enum class Side {
    SOUTH {
        override fun opposite(): Side = NORTH
    },
    NORTH {
        override fun opposite(): Side = SOUTH
    };

    abstract fun opposite(): Side
}