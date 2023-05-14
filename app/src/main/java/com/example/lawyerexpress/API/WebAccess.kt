package com.example.lawyerexpress.API

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WebAccess {
    val LawyerService:LawyerExpressService by lazy {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl("http://lawyerexpress.julioplacas.com/lawyerexpress/")
            .build()

        return@lazy retrofit.create(LawyerExpressService::class.java)
    }
}