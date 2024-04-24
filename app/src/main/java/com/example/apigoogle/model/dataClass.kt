package com.example.apigoogle.model

data class Marcador(
    val id: String,
    var nom:String,
    var descripcio:String,
    var latitut: Double,
    var longitud:Double,
    var tipus: String,
    val owner:String = Authenticaion().getUID()!!) {
    constructor():this("","", "", 0.0, 0.0, "baseline_park_24")
}