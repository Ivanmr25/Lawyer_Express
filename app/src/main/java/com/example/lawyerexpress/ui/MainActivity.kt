package com.example.lawyerexpress.ui

import android.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.example.lawyerexpress.Fragmentos.RegisterFragment
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.ViewModel.MainViewModel
import com.example.lawyerexpress.databinding.ActivityMainBinding
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var shareP: SharedPreferences
    private lateinit var viewModel: MainViewModel
    private var usuario: Abogado? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        shareP = getSharedPreferences("datos", Context.MODE_PRIVATE)
        setSupportActionBar(binding.toolbar)
        getUsuarioSH()
        binding.rl.buttonRegistrarse.setOnClickListener {
            PasaraFramento()
        }

        binding.rl.buttonInicioSesion.setOnClickListener {
            val abogado = Abogado(binding.rl.editTextNumeroColegiado.text.toString().toInt(),"",0,0F,0F,binding.rl.editTextPassword.text.toString())
            IniciarSesion(abogado)
        }
        if (usuario != null){
            PasarASegunda(usuario!!)
        }
    }

    private fun IniciarSesion(abogado: Abogado) {
        viewModel.getUserByNickAndPass(abogado).observe(this, Observer { it ->

                this.usuario = it
                if (usuario != null) {
                    saveUsuarioSH(usuario!!)
                    PasarASegunda(usuario!!)
                }else{
                    Toast.makeText(this,"No existe usuario porfavor registrese",Toast.LENGTH_SHORT).show()
                }

        })
    }

    private fun saveUsuarioSH(abogado: Abogado) {
        val editor = shareP.edit()
        editor.putString("usuario", Gson().toJson(abogado))
        editor.commit()
        Toast.makeText(this,"Login o Registro correcto",Toast.LENGTH_SHORT).show()

    }

    private fun getUsuarioSH() {
        val usuarioTXT = shareP.getString("usuario","nosta")
        if (!usuarioTXT.equals("nosta")){
            usuario = Gson().fromJson(usuarioTXT, Abogado::class.java)
            Toast.makeText(this,"Welcome ${usuario!!.nombre}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun PasarASegunda(abogado: Abogado) {
        val intent = Intent(this, LawyerExpress::class.java)
        intent.putExtra("abogado",abogado)
        startActivity(intent)
    }

    private fun PasaraFramento() {
        val myFragment = RegisterFragment()


        // Obtener el FragmentManager
        val fragmentManager = supportFragmentManager



        // Iniciar la transacción de fragmento
        val fragmentTransaction = fragmentManager.beginTransaction()


        // Reemplazar el contenido de la Activity por el nuevo fragmento
        fragmentTransaction.replace(R.id.content, myFragment)

        fragmentTransaction.addToBackStack(null)

        // Confirmar la transacción
        fragmentTransaction.commit()
    }


}