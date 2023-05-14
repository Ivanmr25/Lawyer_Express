package com.example.lawyerexpress.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.lawyerexpress.Adapters.ViewPageAdapter
import com.example.lawyerexpress.Fragmentos.ChatFragment
import com.example.lawyerexpress.Fragmentos.MapFragment
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.Model.Telefono
import com.example.lawyerexpress.R

import com.example.lawyerexpress.databinding.ActivityLawyerExpressBinding
import com.google.android.material.tabs.TabLayoutMediator

class LawyerExpress : AppCompatActivity() {

    private lateinit var binding: ActivityLawyerExpressBinding
    public var abogado: Abogado? = null
    private lateinit var shareP: SharedPreferences
    private   var telefono: Telefono? = null
    private val icons = arrayOf(R.drawable.mapa,android.R.drawable.stat_notify_chat)
    private val titles = arrayOf("Localizacion  ","Chat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLawyerExpressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shareP = getSharedPreferences("datos", Context.MODE_PRIVATE)
        abogado = intent.getSerializableExtra("abogado") as Abogado?
        title = "Welcome-${abogado!!.nombre}"
        setupViewPager(binding.viewPager)
        setupTabs()
        setSupportActionBar(binding.toolbar)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
    private fun setupViewPager(viewPager: ViewPager2) {
        val adapter = ViewPageAdapter(this)
        val chatFragment = ChatFragment()
        chatFragment.arguments = Bundle().apply {
            putSerializable("abogado", abogado)
        }

        adapter.addFragment(MapFragment())
        adapter.addFragment(chatFragment)

        viewPager.adapter = adapter
    }

    private fun setupTabs() {
        val tabLayout = binding.tabs
        val tabLayoutMediator = TabLayoutMediator(tabLayout, binding.viewPager) { tab, i ->
            tab.icon = ContextCompat.getDrawable(this, icons.get(i))

            // Si queremos iconos nuestros en color:
            val filter = PorterDuffColorFilter(0, PorterDuff.Mode.SCREEN)
            tab.icon!!.colorFilter = filter

            tab.text = titles.get(i)
        }

        // Establecer el color del texto de la pestaña
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)

        // Establecer el color de la barra inferior de la pestaña
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.HHM))

        // Adjuntar el mediador de la pestaña
        tabLayoutMediator.attach()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.fab.hide()
                } else {
                    binding.fab.show()
                }
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_law, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                removeUsuarioSH()
                return true
            }

            // Manejar otras acciones del menú aquí si es necesario
        }
        return super.onOptionsItemSelected(item)
    }
    private fun removeUsuarioSH() {
        val editor = shareP.edit()
        editor.remove("usuario")
        editor.commit()
        Toast.makeText(this,"Goodbye ${abogado!!.nombre}",Toast.LENGTH_SHORT).show()
        abogado = null
        GetBack()


    }

    private fun GetBack() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
    }
}
