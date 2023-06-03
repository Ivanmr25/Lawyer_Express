package com.example.lawyerexpress.API

import android.util.Log
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.Amigo
import com.example.lawyerexpress.Model.PartidoJudicial
import com.example.lawyerexpress.Model.Telefono
import okhttp3.MediaType
import okhttp3.RequestBody

class MainRepository {
    val service = WebAccess.LawyerService


    //Funcion para obtener partidos judiciales devuelve una lista de partidos judiciales

    suspend fun getPartidos(): List<PartidoJudicial> {
        val webResponse = service.getPartidos().await()
        if (webResponse.isSuccessful) {

            return webResponse.body()!!.partidos
        }
        return emptyList()
    }


    //Funcion para conseguir los amigos de un abogado mediante su numero de colegiado devuelve una lista de Abogados

    suspend fun getAmigos(numero_colegiado:Int): List<Abogado> {
        val webResponse = service.getAmigos(numero_colegiado).await()
        if (webResponse.isSuccessful) {

            return webResponse.body()!!.amigos
        }
        return emptyList()
    }



 //Funcion que se encarga de comprobar si el usuario existe en la base de datos en el caso de que no devuelve un nulo

    suspend fun getAbogadoById(usuario: Abogado): Abogado? {
        var usuarioresponse:Abogado? = null
        val webResponse = service.getUserByNickAndPass(usuario.numero_colegiado.toString(),usuario.pass).await()
        if (webResponse.isSuccessful) {

            usuarioresponse = webResponse.body()!!.abogado
        }
        return usuarioresponse
    }

    //Funcion para insertar un abogado en la base de datos
    suspend fun saveAbogado(abogado: Abogado): Abogado? {
        var usuarioresponse:Abogado? = null
        val webResponse = service.saveAbogado(abogado).await()
        if (webResponse.isSuccessful) {

            usuarioresponse = webResponse.body()!!.abogado
        }

        return usuarioresponse
    }

    //Funcion para insertar un telefono en la base de datos
    suspend fun saveTelefono(telefono: Telefono): Telefono? {
        var telefonoresponse:Telefono? = null
        val webResponse = service.saveTelefono(telefono).await()
        if (webResponse.isSuccessful) {

            telefonoresponse = webResponse.body()!!.telefono
        }
        return telefonoresponse
    }


    //Funcion para guardar a un amigo mediante su numero de telefono
    suspend fun saveAmigo(numero_colegiado: Int, numero: Int): Amigo? {
        var amigoResponse: Amigo? = null
        //Se crea el cuerpo de la solicitud (requestBody) como un JSON con el número de colegiado
        val requestBody = RequestBody.create(MediaType.parse("application/json"), "{\"numero_colegiado\":$numero_colegiado}")
        val webResponse = service.saveAmigo(requestBody, numero).await()
        if (webResponse.isSuccessful) {

            amigoResponse = webResponse.body()?.amigo

        }
        return amigoResponse
    }

    //Funcion para guardar la localizacion del abogado
    suspend fun updateLocation(numeroColegiado: Int, latitud: Float, longitud: Float): Abogado? {
        var abogadoResponse: Abogado? = null
        //Se crea el cuerpo de la solicitud (requestBody) como un JSON mediante la latitud y la longitud del abogado
        val requestBody = RequestBody.create(MediaType.parse("application/json"), "{\"latitud\": $latitud, \"longitud\": $longitud}")
        val webResponse = service.UpdateLocation(requestBody, numeroColegiado).await()
        if (webResponse.isSuccessful) {
            abogadoResponse = webResponse.body()?.abogado
        }
        return abogadoResponse
    }
}