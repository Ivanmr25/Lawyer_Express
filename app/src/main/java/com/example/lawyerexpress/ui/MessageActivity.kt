package com.example.lawyerexpress.ui

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lawyerexpress.Adapters.MessageAdapter
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.Mensaje
import com.example.lawyerexpress.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.EventListener

class MessageActivity : AppCompatActivity() {
    private lateinit var abogado: Abogado
    private lateinit var amigo: Abogado
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messages: MutableList<Mensaje> = mutableListOf()
    private lateinit var messagesRef: CollectionReference
    private lateinit var querySender: Query
    private lateinit var queryReceiver: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        amigo = intent.getSerializableExtra("amigo") as Abogado
        abogado = intent.getSerializableExtra("abogado") as Abogado
        title = amigo.nombre

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        chatRecyclerView.layoutManager = layoutManager

        messageAdapter = MessageAdapter(messages, abogado.numero_colegiado.toString())
        chatRecyclerView.adapter = messageAdapter

        //Obtener la instancia de la base de datos de Firebase
        val db = FirebaseFirestore.getInstance()
        messagesRef = db.collection("mensajes")

        // Crear una consulta para obtener los mensajes enviados por el remitente actual
        querySender = messagesRef
            .whereEqualTo("remitente", abogado.numero_colegiado.toString())
            .whereEqualTo("destinatario", amigo.numero_colegiado.toString())
            .orderBy("fecha_envio", Query.Direction.DESCENDING)

        // Crear una consulta para obtener los mensajes recibidos por el remitente actual
        queryReceiver = messagesRef
            .whereEqualTo("remitente", amigo.numero_colegiado.toString())
            .whereEqualTo("destinatario", abogado.numero_colegiado.toString())
            .orderBy("fecha_envio", Query.Direction.DESCENDING)

        val senderMessagesListener = EventListener<QuerySnapshot> { senderSnapshot, senderError ->
            if (senderError != null) {
                Log.e(TAG, "Error al escuchar los mensajes enviados", senderError)
                return@EventListener
            }

            // Limpiar la lista de mensajes enviados antes de agregar los nuevos mensajes para evitar duplicacion de mensajes
            messages.removeAll { it.remitente == abogado.numero_colegiado.toString() }

            val senderMessages = senderSnapshot?.documents?.mapNotNull { document ->
                document.toObject(Mensaje::class.java)
            }

            if (senderMessages != null) {
                messages.addAll(senderMessages)
                messages.sortBy { it.fecha_envio }
                messageAdapter.notifyDataSetChanged()
            }
        }

        // Listener para escuchar los cambios en los mensajes recibidos por el remitente
        val receiverMessagesListener = EventListener<QuerySnapshot> { receiverSnapshot, receiverError ->
            if (receiverError != null) {
                Log.e(TAG, "Error al escuchar los mensajes recibidos", receiverError)
                return@EventListener
            }

            // Limpiar la lista de mensajes recibidos antes de agregar los nuevos mensajes para evitar duplicacion de mensajes
            messages.removeAll { it.remitente == amigo.numero_colegiado.toString() }

            val receiverMessages = receiverSnapshot?.documents?.mapNotNull { document ->
                document.toObject(Mensaje::class.java)
            }

            if (receiverMessages != null) {
                messages.addAll(receiverMessages)
                messages.sortBy { it.fecha_envio }
                messageAdapter.notifyDataSetChanged()
            }
        }

        querySender.addSnapshotListener(senderMessagesListener)
        queryReceiver.addSnapshotListener(receiverMessagesListener)





        //Boton que se encarga de insertar un mensaje a la base de datos
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                val currentTime = Calendar.getInstance().time
                val formattedDate = SimpleDateFormat(
                    "dd 'de' MMMM 'de' yyyy, HH:mm:ss z",
                    Locale.getDefault()
                ).format(currentTime)

                val message = Mensaje(
                    messageText,
                    amigo.numero_colegiado.toString(),
                    formattedDate,
                    abogado.numero_colegiado.toString()
                )

                messagesRef.add(message)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "Mensaje enviado con ID: ${documentReference.id}")
                        messageEditText.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al enviar el mensaje", e)
                    }
            }
        }
    }

    companion object {
        private const val TAG = "MessageActivity"
    }
}



