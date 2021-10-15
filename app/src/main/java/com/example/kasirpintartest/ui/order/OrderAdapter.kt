package com.example.kasirpintartest.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirpintartest.data.entity.Order
import com.example.kasirpintartest.databinding.OrderItemBinding

class OrderAdapter(private val onClick: (Order, Int, Int) -> Unit) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    companion object {
        const val LOAD = 100
        const val DELETE = 200
    }

    var data = ArrayList<Order>()
    fun addData(data: List<Order>) {
        this.data.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        val binding =
            OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Order) {
            with(binding) {
                tvOrderId.text = data.id.toString()
                btnLoad.setOnClickListener {
                    onClick(data, LOAD, adapterPosition)
                }
                btnDelete.setOnClickListener {
                    onClick(data, DELETE, adapterPosition)
                }
            }
        }
    }

    fun removeItem(position: Int) {
        this.data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.data.size)
        notifyDataSetChanged()
    }
}