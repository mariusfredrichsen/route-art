package com.example





data class Line(
    val points: List<Point>
) {
    fun isPartlyInsideSquare(square: List<Point>): Boolean {
        return points.any({point ->
            point.isInsideSquare(square)
        })
    }

    fun isFullyInsideSquare(square: List<Point>): Boolean {
        return points.all({point -> 
            point.isInsideSquare(square)
        })
    }
}