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

class MainViewModel: ViewModel() {
    private var repository: MainRepository = MainRepository()

    private val _partidos = MutableLiveData<List<PartidoJudicial>>()
    val partidos: LiveData<List<PartidoJudicial>> = _partidos


    init {
        getPartidoJudicial()
    }

    fun getPartidoJudicial() {
        viewModelScope.launch {
            _partidos.value = repository.getPartidos()
        }
    }




    fun getAmigos(numero_colegiado:Int): MutableLiveData<List<Abogado>>{
        val amigoresponse = MutableLiveData<List<Abogado>>()
        GlobalScope.launch(Dispatchers.Main) {
            amigoresponse.value = repository.getAmigos(numero_colegiado)

        }
        return  amigoresponse
    }
    fun getUserByNickAndPass(usuario: Abogado):MutableLiveData<Abogado> {
        val usuarioresponse= MutableLiveData<Abogado>()
        GlobalScope.launch(Dispatchers.Main) {
            usuarioresponse.value = repository.getAbogadoById(usuario)

        }
        return usuarioresponse
    }

    fun saveAbogado(usuario: Abogado):MutableLiveData<Abogado> {
        val usuarioresponse= MutableLiveData<Abogado>()
        GlobalScope.launch(Dispatchers.Main) {
            usuarioresponse.value = repository.saveAbogado(usuario)
        }
        return usuarioresponse
    }

    fun saveTelefono(telefono: Telefono):MutableLiveData<Telefono> {
        val telefonoresponse= MutableLiveData<Telefono>()
        GlobalScope.launch(Dispatchers.Main) {
            telefonoresponse.value = repository.saveTelefono(telefono)
        }
        return telefonoresponse
    }

    fun saveAmigo(numero_colegiado: Int, numero: Int): MutableLiveData<Amigo> {
        val amigoResponse = MutableLiveData<Amigo>()
        GlobalScope.launch(Dispatchers.Main) {
            amigoResponse.value = repository.saveAmigo(numero_colegiado, numero)
        }
        return amigoResponse
    }



}