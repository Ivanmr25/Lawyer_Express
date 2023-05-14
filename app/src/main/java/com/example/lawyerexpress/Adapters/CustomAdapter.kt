package com.example.lawyerexpress.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.R

class CustomAdapter(
    val context: Context,
    val layout: Int
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var dataList: List<Abogado> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setAmigos(recursos: List<Abogado>) {
        dataList = recursos // Asignar los nuevos datos a la lista
        notifyDataSetChanged()
    }

    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Abogado) {
            val tvnombrerowrecurso = itemView.findViewById<TextView>(R.id.tvNombreContacto)
            tvnombrerowrecurso.text = dataItem.nombre
            itemView.tag = dataItem
        }
    }
}
