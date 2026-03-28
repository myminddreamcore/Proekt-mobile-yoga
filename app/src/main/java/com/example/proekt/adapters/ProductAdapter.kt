package com.example.proekt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.R
import com.example.proekt.StaticUser
import com.example.proekt.api.ApiClient
import com.example.proekt.models.Basket
import com.example.proekt.models.Product
import com.squareup.picasso.Picasso

class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvProductCol: TextView = itemView.findViewById(R.id.tvProductCol)
        val btnAddToCart: AppCompatButton = itemView.findViewById(R.id.btnAddToCart)

        fun bind(product: Product) {
            Picasso.get()
                .load(product.photoMobile)
                .placeholder(R.drawable.samolet)
                .error(R.drawable.logo)
                .into(ivProduct)

            tvProductName.text = product.name
            tvProductPrice.text = "${product.price} руб."
            tvProductCol.text = "В наличии: ${product.col}"

            btnAddToCart.setOnClickListener {
                onAddToCart(product)
            }

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}