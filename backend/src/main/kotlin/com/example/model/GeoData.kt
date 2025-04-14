package com.example

import kotlinx.serialization.Serializable


@Serializable
data class GeoData(
    val type: String,
    val coordinates: List<List<List<Float>>>
)