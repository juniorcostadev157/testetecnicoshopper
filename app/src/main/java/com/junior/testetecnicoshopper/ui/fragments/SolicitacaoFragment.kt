package com.junior.testetecnicoshopper.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.junior.testetecnicoshopper.R
import com.junior.testetecnicoshopper.databinding.FragmentSolicitacaoBinding
import com.junior.testetecnicoshopper.model.SolicitacaoViagem
import com.junior.testetecnicoshopper.model.ViagemResponse
import com.junior.testetecnicoshopper.service.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SolicitacaoFragment : Fragment() {

    private var _binding: FragmentSolicitacaoBinding? = null
    private val binding get() = _binding

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSolicitacaoBinding.inflate(inflater, container, false)

        binding?.btnEstimate?.setOnClickListener {
            if (isLoading) return@setOnClickListener // Evitar múltiplos cliques

            val customerId = binding!!.inputIDUser.text.toString().trim()
            val origin = binding!!.inputOrigem.text.toString().trim()
            val destination = binding!!.InputDestination.text.toString().trim()

            if (customerId.isNotEmpty() && origin.isNotEmpty() && destination.isNotEmpty()) {
                if (origin.equals(destination, ignoreCase = true)) {
                    Toast.makeText(
                        requireContext(),
                        "O endereço de destino não pode ser igual ao de origem.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    estimateRide(customerId, origin, destination)
                }
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding?.root
    }

    private fun estimateRide(customerId: String, origin: String, destination: String) {
        setLoading(true)

        val request = SolicitacaoViagem(customer_id = customerId, origin = origin, destination = destination)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instace.estimateRide(request)

                withContext(Dispatchers.Main) {
                    setLoading(false)

                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("API_RESPONSE", "Resposta da API: $body")

                        if (body != null) {
                            if (body.options.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Nenhum motorista disponível no momento.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                navigateToOptions(body, customerId, origin, destination)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Resposta vazia da API.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        handleApiError(response.errorBody()?.string())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setLoading(false)
                    Log.e("API_EXCEPTION", "Erro: ${e.message}", e)
                    Toast.makeText(requireContext(), "Erro de conexão com a API.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToOptions(body: ViagemResponse, customerId: String, origin: String, destination: String) {

        body.options.forEach { motorista ->

        }

        val bundle = Bundle().apply {
            putParcelableArrayList("motoristas", ArrayList(body.options))
            putParcelable("routeResponse", body.routeResponse)
            putString("customerId", customerId)
            putString("origin", origin)
            putString("destination", destination)
        }

        val fragment = OptionMotoristaFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }



    private fun handleApiError(errorBody: String?) {
        val errorMessage = errorBody ?: "Erro desconhecido"
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        Log.e("API_ERROR", errorMessage)
    }

    private fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding?.btnEstimate?.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
