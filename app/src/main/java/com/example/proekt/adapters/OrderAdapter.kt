package com.example.proekt.adapters

import Order
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.R
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private var orders: List<Order>,
    private val onMoreClick: (Order, View) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val tvDeliveryType: TextView = itemView.findViewById(R.id.tvDeliveryType)
        val tvOrderItems: TextView = itemView.findViewById(R.id.tvOrderItems)
        val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        val btnMore: AppCompatButton = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderNumber.text = "Заказ #${order.id}"
        holder.tvOrderStatus.text = order.status ?: "В обработке"

        when (order.status?.lowercase()) {

            "доставлен", "завершен", "выполнен" -> holder.tvOrderStatus.setTextColor(
                holder.itemView.context.getColor(R.color.black)
            )
            "отменен" -> holder.tvOrderStatus.setTextColor(
                holder.itemView.context.getColor(R.color.red)
            )
            else -> holder.tvOrderStatus.setTextColor(
                holder.itemView.context.getColor(R.color.red)
            )
        }

        order.date?.let {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
            holder.tvOrderDate.text = dateFormat.format(it)
        } ?: run {
            holder.tvOrderDate.text = "Дата не указана"
        }

        holder.tvDeliveryType.text = order.typeOrder ?: "Не указано"

        val totalItems = order.order?.sumOf { it.col_tovar } ?: 0
        holder.tvOrderItems.text = "$totalItems товара"

        val totalPrice = order.order?.sumOf { it.price * it.col_tovar } ?: 0.0
        holder.tvOrderTotal.text = "${totalPrice.toInt()} руб"

        holder.btnMore.setOnClickListener {
            onMoreClick(order, it)
        }


    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}