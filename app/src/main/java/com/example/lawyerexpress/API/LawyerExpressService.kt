package com.example.lawyerexpress.API

import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.Respuesta
import com.example.lawyerexpress.Model.Telefono
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface LawyerExpressService {

    @GET("partidos")
    fun getPartidos(): Deferred<Response<Respuesta>>

    @GET("amigos/{numero_colegiado}")
    fun getAmigos(@Path("numero_colegiado") numero_colegiado: Int): Deferred<Response<Respuesta>>

    @GET("abogadouser/{numero_colegiado}")
    fun getUserByNickAndPass( @Path("numero_colegiado")numero_colegiado:String ,
                              @Query ("pass") pass: String): Deferred<Response<Respuesta>>

    @POST("abogadouser")
    fun saveAbogado(@Body abogado: Abogado): Deferred<Response<Respuesta>>

    @POST("telefono")
    fun saveTelefono(@Body telefono: Telefono): Deferred<Response<Respuesta>>
}

