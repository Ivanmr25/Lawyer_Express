package com.example.lawyerexpress.API

import android.util.Log
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.PartidoJudicial
import com.example.lawyerexpress.Model.Telefono

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
                Log.d("Ivan","$webResponse")
            return webResponse.body()!!.amigos
        }
        return emptyList()
    }




    suspend fun getAbogadoById(usuario: Abogado): Abogado? {
        var usuarioresponse:Abogado? = null
        val webResponse = service.getUserByNickAndPass(usuario.numero_colegiado.toString(),usuario.pass).await()
        if (webResponse.isSuccessful) {
            Log.d("Ivan","$webResponse")
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
            Log.d("Ivan","$webResponse")
            telefonoresponse = webResponse.body()!!.telefono
        }
        return telefonoresponse
    }
}