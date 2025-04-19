package com.example




data class Point(
    val lon: Float,
    val lat: Float,
) {
    fun isInsideSquare(square: List<Point>): Boolean {
        val bottomLeft = square[0]
        val topRight = square[2]

        return lon >= bottomLeft.lon && lon <= topRight.lon && lat >= bottomLeft.lat && lat <= topRight.lat
    }
}