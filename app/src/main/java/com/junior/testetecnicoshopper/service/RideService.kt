package com.junior.testetecnicoshopper.service

import ConfirmRideRequest
import ConfirmRideResponse
import com.junior.testetecnicoshopper.model.HistoricoViagemResponse

import com.junior.testetecnicoshopper.model.SolicitacaoViagem
import com.junior.testetecnicoshopper.model.ViagemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface RideService{
    @POST("ride/estimate")
    suspend fun estimateRide(@Body request: SolicitacaoViagem):Response<ViagemResponse>

    @PATCH("ride/confirm")
    suspend fun confirmRide(@Body request: ConfirmRideRequest): Response<ConfirmRideResponse>

    @GET("ride/{customer_id}")
    suspend fun getHistoricoViagens(
        @Path("customer_id") customerId: String,
        @Query("driver_id") driverId: Int? = null
    ): Response<HistoricoViagemResponse>
}