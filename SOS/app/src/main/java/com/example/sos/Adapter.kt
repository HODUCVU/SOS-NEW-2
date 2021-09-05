package com.example.sos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val dataSet: List<DataContact>)
    : RecyclerView.Adapter<Adapter.ViewHolderItem>() {
    inner class ViewHolderItem(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val numberPhone: TextView = view.findViewById(R.id.numberPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, null)
        return ViewHolderItem(view)
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        val item = dataSet[position]
        holder.name.text = item.name
        holder.numberPhone.text = item.phoneNumber
    }

    override fun getItemCount() : Int = dataSet.size
}