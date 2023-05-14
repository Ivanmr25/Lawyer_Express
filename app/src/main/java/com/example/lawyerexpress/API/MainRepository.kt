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
    suspend fun getPartidos(): List<PartidoJudicial> {
        val webResponse = service.getPartidos().await()
        if (webResponse.isSuccessful) {

            return webResponse.body()!!.partidos
        }
        return emptyList()
    }

    suspend fun getAmigos(numero_colegiado:Int): List<Abogado> {
        val webResponse = service.getAmigos(numero_colegiado).await()
        if (webResponse.isSuccessful) {

            return webResponse.body()!!.amigos
        }
        return emptyList()
    }




    suspend fun getAbogadoById(usuario: Abogado): Abogado? {
        var usuarioresponse:Abogado? = null
        val webResponse = service.getUserByNickAndPass(usuario.numero_colegiado.toString(),usuario.pass).await()
        if (webResponse.isSuccessful) {

            usuarioresponse = webResponse.body()!!.abogado
        }
        return usuarioresponse
    }
    suspend fun saveAbogado(abogado: Abogado): Abogado? {
        var usuarioresponse:Abogado? = null
        val webResponse = service.saveAbogado(abogado).await()
        if (webResponse.isSuccessful) {

            usuarioresponse = webResponse.body()!!.abogado
        }

        return usuarioresponse
    }

    suspend fun saveTelefono(telefono: Telefono): Telefono? {
        var telefonoresponse:Telefono? = null
        val webResponse = service.saveTelefono(telefono).await()
        if (webResponse.isSuccessful) {

            telefonoresponse = webResponse.body()!!.telefono
        }
        return telefonoresponse
    }

    suspend fun saveAmigo(numero_colegiado: Int, numero: Int): Amigo? {
        var amigoResponse: Amigo? = null
        val requestBody = RequestBody.create(MediaType.parse("application/json"), "{\"numero_colegiado\":$numero_colegiado}")
        val webResponse = service.saveAmigo(requestBody, numero).await()
        if (webResponse.isSuccessful) {

            amigoResponse = webResponse.body()?.amigo

        }
        return amigoResponse
    }
}