package com.example.lawyerexpress.Model

import java.io.Serializable

data class Respuesta(
    var abogado:Abogado,
    var telefono:Telefono,
    var partidos:List<PartidoJudicial>,
    var amigos:List<Abogado>
)

data class  Abogado(

    val numero_colegiado: Int,
    val nombre: String,
    val partidojudicial_id: Int,
    val latitud: Float?,
    val longitud: Float?,
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