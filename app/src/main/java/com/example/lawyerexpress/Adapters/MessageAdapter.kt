package com.example.lawyerexpress.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lawyerexpress.Model.Mensaje
import com.example.lawyerexpress.R


//Adapter que se encarga de mostrar los mensajes recibidos y enviados en una card cada uno y todo esto a partir de una lista de mensajes

class MessageAdapter(private val messages: List<Mensaje>, private val currentUser: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderCard: CardView = itemView.findViewById(R.id.senderCard)
        private val receiverCard: CardView = itemView.findViewById(R.id.receiverCard)
        private val senderMessageText: TextView = itemView.findViewById(R.id.senderMessageText)
        private val receiverMessageText: TextView = itemView.findViewById(R.id.receiverMessageText)
        private val senderTimestamp: TextView = itemView.findViewById(R.id.senderTimestamp)
        private val receiverTimestamp: TextView = itemView.findViewById(R.id.receiverTimestamp)

        fun bind(message: Mensaje) {
            if (message.remitente == currentUser) {
                // Mostrar la tarjeta del mensaje del remitente
                senderCard.visibility = View.VISIBLE
                receiverCard.visibility = View.GONE
                senderMessageText.text = message.contenido
                senderTimestamp.text = message.fecha_envio
            } else {
                // Mostrar la tarjeta del mensaje del destinatario
                senderCard.visibility = View.GONE
                receiverCard.visibility = View.VISIBLE
                receiverMessageText.text = message.contenido
                receiverTimestamp.text = message.fecha_envio
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}