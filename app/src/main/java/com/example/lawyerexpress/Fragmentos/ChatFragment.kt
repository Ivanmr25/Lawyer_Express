package com.example.lawyerexpress.Fragmentos

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lawyerexpress.Adapters.CustomAdapter
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.R
import com.example.lawyerexpress.ViewModel.MainViewModel
import com.example.lawyerexpress.ui.LawyerExpress
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType
import okhttp3.RequestBody

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {

    private lateinit var adapter: CustomAdapter
    private lateinit var amigos: List<Abogado>
    private var abogado: Abogado? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            abogado = it.getSerializable("abogado") as Abogado?
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        GetAmigos()
        initRv(view)
        val buttonAdd = view.findViewById<FloatingActionButton>(R.id.fab)

            buttonAdd.setOnClickListener {
                DialogoInsertarAmigo()
            }



    }

    private fun DialogoInsertarAmigo() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Insertar amigo")
        val ll = LinearLayout(requireContext())
        ll.setPadding(30, 30, 30, 30)
        ll.orientation = LinearLayout.VERTICAL

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 50, 0, 50)

        val textInputLayoutPhone = TextInputLayout(requireContext())
        textInputLayoutPhone.layoutParams = lp
        val etPhone = EditText(requireContext())
        etPhone.setPadding(0, 80, 0, 80)
        etPhone.textSize = 20.0F
        etPhone.hint = "Número de teléfono"
        textInputLayoutPhone.addView(etPhone)

        ll.addView(textInputLayoutPhone)

        builder.setView(ll)

        builder.setPositiveButton("Aceptar") { dialog, which ->
            val phoneNumber = etPhone.text.toString()
            InsertAmigo(abogado!!.numero_colegiado,phoneNumber.toInt())


        }

        builder.setNegativeButton("Cancelar") { dialog, which ->

        }

        builder.show()

    }

    private fun InsertAmigo(numeroColegiado: Int, numero: Int) {

        viewModel.saveAmigo(numeroColegiado, numero).observe(viewLifecycleOwner) { amigo ->
            amigo?.let {
                if (it.amigo_Id != null) {
                    Toast.makeText(requireContext(), "Amigo añadido", Toast.LENGTH_SHORT).show()
                    GetAmigos()
                    refreshRecyclerView()
                } else {
                    Toast.makeText(requireContext(), "No existe nadie registrado, por favor introduzca otro teléfono", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun refreshRecyclerView() {
        adapter.notifyDataSetChanged()
    }

    private fun GetAmigos() {
        viewModel.getAmigos(abogado!!.numero_colegiado).observe(viewLifecycleOwner) { it ->
            it?.let {
                amigos = it
                if (amigos.isEmpty()) {
                    Toast.makeText(requireContext(), "No tienes amigos", Toast.LENGTH_SHORT).show()
                } else {
                    showAmigos(amigos)
                }
            }
        }
    }

    private fun showAmigos(amigos: List<Abogado>) {
        adapter.setAmigos(amigos)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }



    private fun initRv(view: View) {
        adapter = CustomAdapter(abogado,requireActivity(), R.layout.row)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvamigos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


    }

}