package com.example


import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val geoData: GeoData = fetchGeoData("converted_coordinates.json")

    val lons = geoData.coordinates.flatten().map({it[0]})
    val lats = geoData.coordinates.flatten().map({it[1]})
    val MIN_LON = lons.min()
    val MAX_LON = lons.max()
    val MIN_LAT = lats.min()
    val MAX_LAT = lats.max() 

    var height = (MAX_LAT-MIN_LAT)
    while (height % 1.0 != 0.0) height *= 10

    var width = (MAX_LON-MIN_LON)
    while (width % 1.0 != 0.0) width *= 10

    val grid = Array((height / 10).toInt()) { ByteArray((width / 1000).toInt() / 8 + 1) }
    println(grid)

    
    println("MIN_LON - ${MIN_LON}\nMAX_LON - ${MAX_LON}\nMIN_LAT - ${MIN_LAT}\nMAX_LAT - ${MAX_LAT}")
}

fun fetchGeoData(fileName: String): GeoData {
    val file = java.io.File("src/main/resources/$fileName")
    val jsonContent = file.readText()
    return Json.decodeFromString(jsonContent)
}
