package com.junior.testetecnicoshopper.model
import Motorista
import RouteResponse
import android.os.Parcelable

import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ViagemResponse(
    val sucess: Boolean,
    val distance: Double,
    val duration: String,
    val options: @RawValue List<Motorista>,
    val routeResponse: @RawValue RouteResponse
) : Parcelable



