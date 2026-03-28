package com.example.proekt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.R

class AdressAdapter(
    private val addresses: List<String>,
    private val onAddressSelected: (String) -> Unit
) : RecyclerView.Adapter<AdressAdapter.AddressViewHolder>() {

    private var selectedPosition = -1

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rbAddress: RadioButton = itemView.findViewById(R.id.rbAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_adress, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]

        holder.rbAddress.text = address
        holder.rbAddress.isChecked = position == selectedPosition

        val currentPosition = position

        holder.rbAddress.setOnClickListener {
            selectedPosition = currentPosition
            notifyDataSetChanged()
            onAddressSelected(address)
        }

        holder.itemView.setOnClickListener {
            selectedPosition = currentPosition
            notifyDataSetChanged()
            onAddressSelected(address)
        }
    }

    override fun getItemCount(): Int = addresses.size

}