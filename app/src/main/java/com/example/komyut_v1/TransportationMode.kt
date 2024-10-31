package com.example.komyut_v1

abstract class TransportationMode(
    val modeID: String,
    val modeType: String,
    val fare: Double
) {
    abstract fun calculateFare(): Double
    abstract fun getAvailability(): String
}

class Jeepney(modeID: String, fare: Double, val routeCode: String) :
    TransportationMode(modeID, "Jeepney", fare) {
    override fun calculateFare() = fare
    override fun getAvailability() = "Available"
}
