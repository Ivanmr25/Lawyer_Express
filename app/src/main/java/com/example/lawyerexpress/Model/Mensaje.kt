package com.example.lawyerexpress.Model


import java.io.Serializable


//Clase Mensaje a parte para poder manejar los mensajes que se insertan y se recuperan del Firebase Firestore
data class Mensaje (
    var contenido: String = "",
    var destinatario: String = "",
    var fecha_envio: String = "",
    var remitente: String = ""
): Serializable {
    // Constructor sin argumentos requerido por Firebase Firestore
    constructor() : this("", "", "", "")
}