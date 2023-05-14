package com.example.lawyerexpress.Fragmentos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.PartidoJudicial
import com.example.lawyerexpress.Model.Telefono
import com.example.lawyerexpress.R
import com.example.lawyerexpress.ViewModel.MainViewModel
import com.example.lawyerexpress.ui.LawyerExpress
import com.google.gson.Gson

class RegisterFragment : Fragment() {
    private lateinit var partidos: List<PartidoJudicial>
    private lateinit var viewModel: MainViewModel
    private  lateinit var abogado: Abogado
    private lateinit var telefono:Telefono
    private lateinit var shareP: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        shareP = requireContext().getSharedPreferences("datos", Context.MODE_PRIVATE)
        SacarPartidos()
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val buttonRegistrarse = view.findViewById<Button>(R.id.buttonRegistrarse)
        val editTextColegiado = view.findViewById<EditText>(R.id.editTextColegiado)
        val editTextNombre =   view.findViewById<EditText>(R.id.editTextNombre)
        val editTextpass  =  view.findViewById<EditText>(R.id.editTextContraseña)
        val editTextTelefono = view.findViewById<EditText>(R.id.editTextTelefono)
        val spinnerPartidoJudicial = view.findViewById<Spinner>(R.id.spinnerPartidoJudicial)


        buttonRegistrarse.setOnClickListener {

            val numeroColegiado = editTextColegiado.text.toString()
            val nombre = editTextNombre.text.toString()
            val telefono = editTextTelefono.text.toString()
            val partidoSeleccionadoPosition = spinnerPartidoJudicial?.selectedItemPosition
            val partidoSeleccionado = partidos[partidoSeleccionadoPosition!!]
            val pass = editTextpass.text.toString()

             CrearUsuario(numeroColegiado, nombre, partidoSeleccionado.id, pass,telefono)



        }
        return view
    }

    private fun PasarASegunda(abogado: Abogado) {
        val intent = Intent(requireContext(), LawyerExpress::class.java)
        intent.putExtra("abogado",abogado)

        startActivity(intent)
    }


    private fun SacarPartidos() {
        viewModel.partidos.observe(viewLifecycleOwner) { it ->
            it?.let {
                partidos = it
                InitSpinner()
            }
        }
    }

    private fun InitSpinner() {
        val nombresPartidos = partidos.map { partido -> partido.nombre }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresPartidos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val spinnerPartidoJudicial =
            requireView().findViewById<Spinner>(R.id.spinnerPartidoJudicial)

        spinnerPartidoJudicial.adapter = adapter
    }




    private fun CrearUsuario(
        numero_colegiado: String,
        nombre: String,
        partidoSeleccionado: Int,
        pass: String,
        telefono: String
    ) {
        abogado = Abogado(numero_colegiado.toInt(),nombre,partidoSeleccionado,0F,0F,pass)
        viewModel.saveAbogado(abogado).observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                abogado = it

                saveUsuarioSH(it)
                CrearTelefono(it.numero_colegiado,telefono)
            }
        })
    }
    private fun CrearTelefono(numero_colegiado: Int, numero: String) {
        telefono = Telefono(numero.toInt(), numero_colegiado)
        viewModel.saveTelefono(telefono).observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                telefono = it
                Log.d("Ivan","$telefono")
                SaveTelefonoSH(telefono)
                PasarASegunda(abogado)

            }
        })
    }

    private fun SaveTelefonoSH(telefono: Telefono) {
        val editor = shareP.edit()
        editor.putString("telefono", Gson().toJson(telefono))
        editor.commit()
    }


    private fun saveUsuarioSH(abogado: Abogado) {
        val editor = shareP.edit()
        editor.putString("usuario", Gson().toJson(abogado))
        editor.commit()

    }


}