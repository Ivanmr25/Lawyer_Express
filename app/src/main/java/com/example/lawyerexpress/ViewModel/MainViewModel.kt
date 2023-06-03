package com.example.lawyerexpress.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lawyerexpress.API.MainRepository
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.Amigo
import com.example.lawyerexpress.Model.PartidoJudicial
import com.example.lawyerexpress.Model.Telefono
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// View Model encargado de manejar las distintas funciones de la API que se aplicaran en las distintas activities.

class MainViewModel: ViewModel() {
    private var repository: MainRepository = MainRepository()

    private val _partidos = MutableLiveData<List<PartidoJudicial>>()
    val partidos: LiveData<List<PartidoJudicial>> = _partidos

    init {
        getPartidoJudicial()
    }

    //Metodo que consigue los partidos judiciales
    fun getPartidoJudicial() {
        viewModelScope.launch {
            _partidos.value = repository.getPartidos()
        }
    }



    //Metodo que consigue los amigos a partir del numero de colegiado
    fun getAmigos(numero_colegiado:Int): MutableLiveData<List<Abogado>>{
        val amigoresponse = MutableLiveData<List<Abogado>>()
        GlobalScope.launch(Dispatchers.Main) {
            amigoresponse.value = repository.getAmigos(numero_colegiado)

        }
        return  amigoresponse
    }

    //Metodo se encarga de comprobar si existe ese usuario
    fun getUserByNickAndPass(usuario: Abogado):MutableLiveData<Abogado> {
        val usuarioresponse= MutableLiveData<Abogado>()
        GlobalScope.launch(Dispatchers.Main) {
            usuarioresponse.value = repository.getAbogadoById(usuario)

        }
        return usuarioresponse
    }

    //Metodo para poder guardar al abogado en la base de datos
    fun saveAbogado(usuario: Abogado):MutableLiveData<Abogado> {
        val usuarioresponse= MutableLiveData<Abogado>()
        GlobalScope.launch(Dispatchers.Main) {
            usuarioresponse.value = repository.saveAbogado(usuario)
        }
        return usuarioresponse
    }

    //Metodo para insertar un numero de telefono a la base de datos
    fun saveTelefono(telefono: Telefono):MutableLiveData<Telefono> {
        val telefonoresponse= MutableLiveData<Telefono>()
        GlobalScope.launch(Dispatchers.Main) {
            telefonoresponse.value = repository.saveTelefono(telefono)
        }
        return telefonoresponse
    }

    //Metodo para guardar un amigo a un abogado especifico a partir de su numero de telefono
    fun saveAmigo(numero_colegiado: Int, numero: Int): MutableLiveData<Amigo> {
        val amigoResponse = MutableLiveData<Amigo>()
        GlobalScope.launch(Dispatchers.Main) {
            amigoResponse.value = repository.saveAmigo(numero_colegiado, numero)
        }
        return amigoResponse
    }

    //Metodo para actualizar la ubicacion que usaremos mas adelante para que lo haga cada 10 segundos
    fun UpdateLocation(numero_colegiado: Int, latitud: Float, longitud: Float): MutableLiveData<Abogado> {
        val abogadoresponse = MutableLiveData<Abogado>()
        GlobalScope.launch(Dispatchers.Main) {
            abogadoresponse.value = repository.updateLocation(numero_colegiado, latitud, longitud)
        }
        return abogadoresponse
    }




}