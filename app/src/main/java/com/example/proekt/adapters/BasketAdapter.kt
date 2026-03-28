package com.example.proekt.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.R
import com.example.proekt.models.Basket
import com.squareup.picasso.Picasso

class BasketAdapter(
    private var cartItems: List<Basket>,
    private val onQuantityIncrease: (Basket) -> Unit,
    private val onQuantityDecrease: (Basket) -> Unit,
    private val onItemDelete: (Basket) -> Unit
) : RecyclerView.Adapter<BasketAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_basket, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val basketItem = cartItems[position]

        Picasso.get()
            .load(basketItem.photoMobile)
            .placeholder(R.drawable.samolet)
            .error(R.drawable.logo)
            .into(holder.ivProduct)

        holder.tvProductName.text = basketItem.name
        holder.tvPrice.text = "${basketItem.price * basketItem.col_tovar} руб."
        holder.tvQuantity.text = basketItem.col_tovar.toString()

        holder.btnMinus.setOnClickListener {
            onQuantityDecrease(basketItem)
        }

        holder.btnPlus.setOnClickListener {
            onQuantityIncrease(basketItem)
        }

        holder.btnDelete.setOnClickListener {
            onItemDelete(basketItem)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newCartItems: List<Basket>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}