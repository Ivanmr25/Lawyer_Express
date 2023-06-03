package com.example.lawyerexpress.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.R
import com.example.lawyerexpress.ui.MessageActivity



//Adapter que se encarga de rellenar las cards con la informacion del amigo en el recycler view

class CustomAdapter(private val abogado: Abogado?,
    val context: Context,
    val layout: Int
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var dataList: List<Abogado> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }


    //Metodo adaptado para poder pasar desde un fragmento tanto datos como a otra pantalla
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { v ->
            val amigo = dataList[position]  // Obtiene el objeto Amigo en la posición actual
            val intent = Intent(v.context, MessageActivity::class.java)
            intent.putExtra("amigo", amigo)  // Agrega el objeto Amigo como dato extra
            intent.putExtra("abogado", abogado)  // Agrega el objeto Abogado como dato extra
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setAmigos(recursos: List<Abogado>) {
        dataList = recursos // Asignar los nuevos datos a la lista
        notifyDataSetChanged()
    }


    // Clase que se encarga de rellenar los recursos definidos en las cards con los datos del objeto en cuestion
    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Abogado) {
            val tvnombrerowrecurso = itemView.findViewById<TextView>(R.id.tvNombreContacto)
            tvnombrerowrecurso.text = dataItem.nombre
            itemView.tag = dataItem
        }

    }
}
