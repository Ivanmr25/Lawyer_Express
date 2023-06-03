package com.example.lawyerexpress.API

import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.Respuesta
import com.example.lawyerexpress.Model.Telefono
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface LawyerExpressService {


    //Conseguir Partidos Judicales
    @GET("partidos")
    fun getPartidos(): Deferred<Response<Respuesta>>


    //Conseguir los amigos de un abogado mediante su numero de colegiado
    @GET("amigos/{numero_colegiado}")
    fun getAmigos(@Path("numero_colegiado") numero_colegiado: Int): Deferred<Response<Respuesta>>


    // Autenticacion de usuario abogado
    @GET("abogadouser/{numero_colegiado}")
    fun getUserByNickAndPass( @Path("numero_colegiado")numero_colegiado:String ,
                              @Query ("pass") pass: String): Deferred<Response<Respuesta>>

    //Registrar un usuario abogado
    @POST("abogadouser")
    fun saveAbogado(@Body abogado: Abogado): Deferred<Response<Respuesta>>

    //Registrar un telefono
    @POST("telefono")
    fun saveTelefono(@Body telefono: Telefono): Deferred<Response<Respuesta>>

    //Guardar a un amigo mediante su numero de telefono
    @POST("amigo/{numero}")
    fun saveAmigo(@Body body: RequestBody, @Path("numero") numero: Int): Deferred<Response<Respuesta>>

    //Guardar la ubicacion del usuario mediante su numero de colegiado y coordenadas
    @POST("ubicacion/{numero_colegiado}")
    fun UpdateLocation(@Body body: RequestBody, @Path("numero_colegiado") numero_colegiado: Int): Deferred<Response<Respuesta>>
}

