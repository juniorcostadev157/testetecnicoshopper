package com.junior.testetecnicoshopper.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class HistoricoViagemResponse(
    val customer_id: String, // ID do cliente
    val rides: @RawValue List<Viagem> // Lista de viagens realizadas
) : Parcelable
