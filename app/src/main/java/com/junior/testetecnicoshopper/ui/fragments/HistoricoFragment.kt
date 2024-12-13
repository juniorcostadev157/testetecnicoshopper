package com.junior.testetecnicoshopper.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.junior.testetecnicoshopper.adapters.AdapterViagem
import com.junior.testetecnicoshopper.databinding.FragmentHistoricoBinding
import com.junior.testetecnicoshopper.model.Viagem
import com.junior.testetecnicoshopper.service.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoricoFragment : Fragment() {

    private var _binding: FragmentHistoricoBinding? = null
    private val binding get() = _binding

    private val listaViagens = mutableListOf<Viagem>()
    private lateinit var adapterViagem: AdapterViagem

    private var motoristaSelecionadoId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoricoBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSpinner()
        setupFilterButton()

        return binding?.root
    }

    private fun setupRecyclerView() {
        adapterViagem = AdapterViagem(requireContext(), listaViagens)
        binding?.recyclerViewViagens?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterViagem
        }
    }

    private fun setupSpinner() {

        val motoristas = listOf("Todos", "Homer Simpson", "Dominic Toretto", "James Bond")
        val motoristaIds = listOf(null, 1, 2, 3)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, motoristas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerMotoristas?.adapter = adapter

        binding?.spinnerMotoristas?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                motoristaSelecionadoId = motoristaIds[position]
                Log.d("HISTORICO_FRAGMENT", "Motorista selecionado: ${motoristas[position]}, ID: $motoristaSelecionadoId")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                motoristaSelecionadoId = null
            }
        }
    }

    private fun setupFilterButton() {
        binding?.btnAplicarFiltro?.setOnClickListener {
            val customerId = binding?.inputUserId?.text.toString().trim()

            if (customerId.isNotEmpty()) {
                applyFilter(customerId, motoristaSelecionadoId)
            } else {
                Toast.makeText(requireContext(), "Por favor, insira o ID do cliente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyFilter(customerId: String, motoristaId: Int?) {
        Log.d("HISTORICO_FRAGMENT", "Aplicando filtro - Cliente: $customerId, MotoristaId: $motoristaId")
        fetchHistoricoViagens(customerId, motoristaId)
    }

    private fun fetchHistoricoViagens(customerId: String, motoristaId: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chamada à API sem filtrar pelo motorista
                val response = RetrofitClient.instace.getHistoricoViagens(customerId, null)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val historicoResponse = response.body()
                        val viagens = historicoResponse?.rides ?: emptyList()

                        // Aplicar filtro local pelo nome do motorista
                        val viagensFiltradas = if (motoristaId != null) {
                            val motoristaSelecionadoNome = when (motoristaId) {
                                1 -> "Homer Simpson"
                                2 -> "Dominic Toretto"
                                3 -> "James Bond"
                                else -> ""
                            }
                            viagens.filter { it.driver.name == motoristaSelecionadoNome }
                        } else {
                            viagens
                        }

                        // Atualizar RecyclerView com as viagens filtradas
                        if (viagensFiltradas.isEmpty()) {
                            listaViagens.clear()
                            adapterViagem.notifyDataSetChanged()
                            Toast.makeText(requireContext(), "Nenhum registro encontrado.", Toast.LENGTH_SHORT).show()
                        } else {
                            listaViagens.clear()
                            listaViagens.addAll(viagensFiltradas)
                            adapterViagem.notifyDataSetChanged()
                            Toast.makeText(requireContext(), "${viagensFiltradas.size} registros encontrados.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        handleApiError(response.code())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao buscar histórico: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HISTORICO_FRAGMENT", "Erro ao buscar histórico", e)
                }
            }
        }
    }


    private fun handleApiError(statusCode: Int) {
        val errorMessage = when (statusCode) {
            400 -> "Parâmetros inválidos."
            404 -> "Nenhum registro encontrado."
            else -> "Erro desconhecido."
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
