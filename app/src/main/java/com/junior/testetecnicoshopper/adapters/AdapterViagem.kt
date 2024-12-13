package com.junior.testetecnicoshopper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.junior.testetecnicoshopper.databinding.ItemViagemBinding
import com.junior.testetecnicoshopper.model.Viagem
import java.text.SimpleDateFormat
import java.util.*

class AdapterViagem(
    private val context: Context,
    private val listaViagem: MutableList<Viagem>
) : RecyclerView.Adapter<AdapterViagem.ViagemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViagemViewHolder {
        val binding = ItemViagemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViagemViewHolder(binding)
    }

    override fun getItemCount(): Int = listaViagem.size

    override fun onBindViewHolder(holder: ViagemViewHolder, position: Int) {
        val viagem = listaViagem[position]

        // Formatar a data para o padrão brasileiro
        val formattedDate = formatDateToBrazilianTime(viagem.date)

        holder.tvDataHora.text = formattedDate
        holder.tvMotorista.text = "Motorista: ${viagem.driver.name}"
        holder.tvOrigem.text = "Origem: ${viagem.origin}"
        holder.tvDestino.text = "Destino: ${viagem.destination}"
        holder.tvDistancia.text = "Distância: ${viagem.distance} km"
        holder.tvDuracao.text = "Duração: ${viagem.duration}"
        holder.tvValor.text = "Valor: R$ ${"%.2f".format(viagem.value)}"
    }

    private fun formatDateToBrazilianTime(dateString: String): String {
        return try {

            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")

            val targetFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            targetFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

            val date = originalFormat.parse(dateString) // Converter para Date
            date?.let { targetFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    inner class ViagemViewHolder(binding: ItemViagemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDataHora = binding.tvDataHora
        val tvMotorista = binding.tvMotorista
        val tvOrigem = binding.tvOrigem
        val tvDestino = binding.tvDestino
        val tvDistancia = binding.tvDistancia
        val tvDuracao = binding.tvDuracao
        val tvValor = binding.tvValor
    }
}
