package com.junior.testetecnicoshopper.ui.fragments

import ConfirmRideRequest
import Motorista
import RouteResponse
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.junior.testetecnicoshopper.R
import com.junior.testetecnicoshopper.adapters.AdapterMotorista
import com.junior.testetecnicoshopper.databinding.FragmentOptionMotoristaBinding
import com.junior.testetecnicoshopper.service.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OptionMotoristaFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentOptionMotoristaBinding? = null
    private val binding get() = _binding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var routeResponse: RouteResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOptionMotoristaBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mapView = binding!!.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        routeResponse = arguments?.getParcelable("routeResponse")
        val motoristas = arguments?.getParcelableArrayList<Motorista>("motoristas")
        val customerId = arguments?.getString("customerId") ?: ""
        val origin = arguments?.getString("origin") ?: ""
        val destination = arguments?.getString("destination") ?: ""


        routeResponse?.routes?.get(0)?.legs?.get(0)?.distanceMeters?.let {
            val distanceKm = it / 1000.0 // Converter metros para quilômetros
            binding?.tvDistancia?.text = "Distância: %.2f km".format(distanceKm)
            Log.d("OptionMotoristaFragment", "Distância: $distanceKm km")
        }


        motoristas?.let { setupRecyclerView(it, customerId, origin, destination) }
    }

    private fun setupRecyclerView(
        motoristas: ArrayList<Motorista>,
        customerId: String,
        origin: String,
        destination: String
    ) {
        val adapter = AdapterMotorista(requireContext(), motoristas) { selectedDriver ->
            validateAndConfirmRide(
                customerId = customerId,
                origin = origin,
                destination = destination,
                motorista = selectedDriver
            )
        }
        binding?.recyclerViewMotoristas?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    private fun validateAndConfirmRide(customerId: String, origin: String, destination: String, motorista: Motorista) {
        Log.d("OptionMotoristaFragment", "Validando viagem para o motorista ${motorista.name}")

        val distanceKm = (routeResponse?.routes?.get(0)?.legs?.get(0)?.distanceMeters ?: 0) / 1000.0

        val minDistance = when (motorista.id) {
            1 -> 1.0
            2 -> 5.0
            3 -> 10.0
            else -> Double.MAX_VALUE // Caso inválido
        }

        if (distanceKm < minDistance) {
            Toast.makeText(
                requireContext(),
                "Erro: A distância da viagem (${"%.2f".format(distanceKm)} km) é menor que a distância mínima aceita (${"%.2f".format(minDistance)} km) pelo motorista.",
                Toast.LENGTH_LONG
            ).show()
            return
        }


        confirmRide(customerId, origin, destination, motorista)
    }

    private fun confirmRide(customerId: String, origin: String, destination: String, motorista: Motorista) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmação de Viagem")
            .setMessage("Deseja confirmar a viagem com o motorista ${motorista.name}?")
            .setPositiveButton("Confirmar") { _, _ ->
                proceedWithRideConfirmation(customerId, origin, destination, motorista)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun proceedWithRideConfirmation(customerId: String, origin: String, destination: String, motorista: Motorista) {
        val request = ConfirmRideRequest(
            customer_id = customerId,
            origin = origin,
            destination = destination,
            distance = routeResponse?.routes?.get(0)?.legs?.get(0)?.distanceMeters ?: 0,
            duration = routeResponse?.routes?.get(0)?.legs?.get(0)?.duration ?: "",
            driver = motorista,
            value = motorista.value
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instace.confirmRide(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Log.d("OptionMotoristaFragment", "Viagem confirmada com sucesso")
                        Toast.makeText(requireContext(), "Viagem confirmada!", Toast.LENGTH_SHORT).show()
                        navigateToHistory()
                    } else {
                        Log.e("OptionMotoristaFragment", "Erro ao confirmar viagem: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Erro ao confirmar viagem.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("OptionMotoristaFragment", "Erro ao conectar com a API: ${e.message}")
                    Toast.makeText(requireContext(), "Erro de conexão com a API.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToHistory() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HistoricoFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        routeResponse?.let { drawRouteOnMap(it) }
    }

    private fun drawRouteOnMap(routeResponse: RouteResponse) {
        val routes = routeResponse.routes ?: return
        val route = routes[0]
        val polyline = decodePolyline(route.legs[0].polyline?.encodedPolyline ?: "")

        val startLocation = LatLng(route.legs[0].startLocation.latitude, route.legs[0].startLocation.longitude)
        val endLocation = LatLng(route.legs[0].endLocation.latitude, route.legs[0].endLocation.longitude)

        googleMap.addMarker(MarkerOptions().position(startLocation).title("Origem"))
        googleMap.addMarker(MarkerOptions().position(endLocation).title("Destino"))

        googleMap.addPolyline(PolylineOptions().addAll(polyline).color(Color.BLUE).width(10f))

        val bounds = LatLngBounds.Builder()
        polyline.forEach { bounds.include(it) }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
