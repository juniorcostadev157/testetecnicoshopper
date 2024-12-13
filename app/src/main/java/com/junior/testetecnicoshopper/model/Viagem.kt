package com.junior.testetecnicoshopper.model

import Motorista

data class Viagem(
    val id: Int,
    val date: String,
    val origin: String,
    val destination: String,
    val distance: Double,
    val duration: String,
    val driver: Motorista,
    val value: Double
)


