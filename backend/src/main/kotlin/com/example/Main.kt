package com.example


import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val tempGeoData: GeoData = fetchGeoData("converted_coordinates.json")
    val geoData = MultiLine(
        lines = tempGeoData.coordinates.map({ line -> 
            Line(
                points = line.map({ point -> 
                    Point(
                        lon = point[0], 
                        lat = point[1],
                    )
                })
            )
        })
    )

    val lons = tempGeoData.coordinates.flatten().map({it[0]})
    val lats = tempGeoData.coordinates.flatten().map({it[1]})
    val MIN_LON = lons.min()
    val MAX_LON = lons.max()
    val MIN_LAT = lats.min()
    val MAX_LAT = lats.max() 

    var height = (MAX_LAT-MIN_LAT)
    while (height % 1.0 != 0.0) height *= 10
    println(height)

    var width = (MAX_LON-MIN_LON)
    while (width % 1.0 != 0.0) width *= 10
    println(width)
    
    println("MIN_LON - ${MIN_LON}\nMAX_LON - ${MAX_LON}\nMIN_LAT - ${MIN_LAT}\nMAX_LAT - ${MAX_LAT}")

    val square = createGeoSquare(10.7414f, 59.9196f, 1f)
    println(square)
    val point = Point(lon = 10.005f, lat = 59.001f)
    println(point.isInsideSquare(square))

    val pointOne = point
    val pointTwo = Point(lon = 11f, lat = 59f)
    val line = Line(
        points = listOf(pointOne, pointTwo)
    )
    println(line.isPartlyInsideSquare(square))
    println(line.isFullyInsideSquare(square))

    val multiLine = MultiLine(
        lines = geoData.getLinesPartlyInsideSquare(square)
    )
    saveMultiLineToJsonFile(multiLine, "test.json")
}

fun fetchGeoData(fileName: String): GeoData {
    val file = java.io.File("src/main/resources/$fileName")
    val jsonContent = file.readText()
    return Json.decodeFromString(jsonContent)
}

fun calculateNewPosition(lonStart: Float, latStart: Float, distance: Float): Point {
    """
        gets new lon and lat from a startlon and startlat based on distance (example: 1km north + 1km east or 3km north + 3km east)
    """
    val R = 6371.0f // radius of earth in km

    val latNew: Float = latStart + (distance / R) * (180f / Math.PI.toFloat())
    val lonNew: Float = lonStart + (distance / R) * (180f / Math.PI.toFloat()) / Math.cos(Math.toRadians(latStart.toDouble())).toFloat()

    return Point(
        lon = lonNew,
        lat = latNew
    )
}

fun createGeoSquare(lonStart: Float, latStart: Float, distance: Float): List<Point> {
    """
        creates a geolocated square based on a start point and a distance
    """
    val start = Point(
        lon = lonStart,
        lat = latStart,
    )
    val end = calculateNewPosition(
        lonStart = lonStart,
        latStart = latStart,
        distance = distance,
    )

    return listOf(
        start,
        Point(
            lon = start.lon,
            lat = end.lat,
        ),
        end,
        Point(
            lon = end.lon,
            lat = start.lat,
        ),
        start,
    )
}



fun saveMultiLineToJsonFile(multiLine: MultiLine, fileName: String) {
    val geoJson = GeoData(
        type = "MultiLineString",
        coordinates = multiLine.lines.map { line ->
            line.points.map { point ->
                listOf(point.lon, point.lat)
            }
        }
    )

    val json = Json.encodeToString(geoJson)
    val file = File("src/main/resources/$fileName")
    file.writeText(json)
}