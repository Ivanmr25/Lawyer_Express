package com.example.lawyerexpress.Model

import java.io.Serializable

//Clases de los objetos que reciben la respuesta de las distintas acciones que realiza la API

data class Respuesta(
    var abogado:Abogado,
    var telefono:Telefono,
    var partidos:List<PartidoJudicial>,
    var amigos:List<Abogado>,
    var amigo: Amigo
)

data class  Abogado(

    val numero_colegiado: Int,
    val nombre: String,
    val partidojudicial_id: Int,
    var latitud: Float?,
    var longitud: Float?,
    val pass: String
): Serializable

data class Telefono(
    val numero:Int,
    val numero_colegiado: Int
):Serializable

data class PartidoJudicial(
    val id:Int,
    val nombre: String

):Serializable

data class Amigo(
    val amigo_Id:Int,
    val numero_colegiado: Int

):Serializable