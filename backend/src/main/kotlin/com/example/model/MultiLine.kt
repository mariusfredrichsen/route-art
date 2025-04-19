package com.example



data class MultiLine(
    val lines: List<Line>
) {
    fun getLinesPartlyInsideSquare(square: List<Point>): List<Line> {
        return lines.filter({line ->
            line.isPartlyInsideSquare(square)
        })
    }
}