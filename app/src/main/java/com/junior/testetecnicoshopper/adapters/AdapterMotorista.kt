package com.junior.testetecnicoshopper.adapters

import Motorista
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.junior.testetecnicoshopper.databinding.ItemMotoristaBinding

class AdapterMotorista(
    val context: Context,
    private val list_motorista: MutableList<Motorista>,
    private val onDriverSelected: (Motorista) -> Unit
) : RecyclerView.Adapter<AdapterMotorista.MotoristaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotoristaViewHolder {
        val itemLista = ItemMotoristaBinding.inflate(LayoutInflater.from(context), parent, false)
        return MotoristaViewHolder(itemLista)
    }

    override fun getItemCount() = list_motorista.size

    override fun onBindViewHolder(holder: MotoristaViewHolder, position: Int) {
        val motorista = list_motorista[position]
        holder.bind(motorista)
    }

    inner class MotoristaViewHolder(private val binding: ItemMotoristaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(motorista: Motorista) {
            binding.tvNomeMotorista.text = motorista.name
            binding.tvDescricaoMotorista.text = motorista.description
            binding.tvVeiculo.text = motorista.vehicle

            val rating = motorista.review?.rating ?: 0.0
            val comment = motorista.review?.comment ?: "Nenhum comentário disponível."


            binding.rbAvaliacao.rating = rating.toFloat()
            binding.tvValor.text = "R$ %.2f".format(motorista.value)
            binding.tvComentario.text = comment

            Log.d("AdapterMotorista", "Motorista: ${motorista.name}, Rating: $rating, Comentário: $comment")

            binding.btnEscolher.setOnClickListener {
                onDriverSelected(motorista)
            }
        }
    }
}
