package com.uselessprojects.chillara

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class resultAdapter(private val list: List<Results>,private val onConnectClicked: (BluetoothDevice) -> Unit): RecyclerView.Adapter<resultAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.name_tv.text = item.name
        holder.amount_tv.text = item.amount
        holder.acceptBtn.setOnClickListener {
            onConnectClicked(item.device) // pass device on button click
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name_tv: TextView = itemView.findViewById(R.id.name_tv)
        val amount_tv: TextView = itemView.findViewById(R.id.amount_tv)
        val acceptBtn: Button = itemView.findViewById(R.id.accept_btn)
    }
}