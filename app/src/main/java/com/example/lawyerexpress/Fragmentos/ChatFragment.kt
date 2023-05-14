package com.example.lawyerexpress.Fragmentos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lawyerexpress.Adapters.CustomAdapter
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.R
import com.example.lawyerexpress.ViewModel.MainViewModel
import com.example.lawyerexpress.ui.LawyerExpress

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
    private lateinit var shareP: SharedPreferences
    private lateinit var amigos: List<Abogado>
    private var abogado: Abogado? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            abogado = it.getSerializable("abogado") as Abogado?
        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        GetAmigos()
        initRv(view)
    }

    private fun initRv(view: View) {
        adapter = CustomAdapter(requireActivity(), R.layout.row)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvamigos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}