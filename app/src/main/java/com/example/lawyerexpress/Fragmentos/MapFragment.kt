package com.example.lawyerexpress.Fragmentos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.lawyerexpress.Model.Abogado
import com.example.lawyerexpress.R
import com.example.lawyerexpress.ViewModel.MainViewModel
import com.example.lawyerexpress.ui.LawyerExpress
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var map: GoogleMap
    private lateinit var viewModel: MainViewModel
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest: LocationRequest
    private var locationPermissionGranted = false
    private var shouldEnableLocation = false
    private val REQUEST_LOCATION_PERMISSION = 1
    private val REQUEST_CHECK_SETTINGS = 2
    private var abogado: Abogado? = null
    private lateinit var amigos: List<Abogado>
    private lateinit var shareP: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            abogado = it.getSerializable("abogado") as Abogado?
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        shareP = requireContext().getSharedPreferences("datos", Context.MODE_PRIVATE)
        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            if (isLocationGranted()) {
                enableLocation()
            } else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
        GetAmigos()
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        // Configurar el listener de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Realizar búsqueda en la lista de amigos
                val foundAmigo = amigos.find { it.nombre.contains(query ?: "", true) }

                // Si se encuentra el amigo, centrar la cámara en su ubicación
                if (foundAmigo != null) {
                    val amigoLocation = LatLng((foundAmigo.latitud ?: 0f).toDouble(), (foundAmigo.longitud ?:0f).toDouble())
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(amigoLocation, 15f)
                    map.animateCamera(cameraUpdate)
                } else {
                    Toast.makeText(requireContext(), "Amigo no encontrado", Toast.LENGTH_SHORT).show()
                }

                // Retornar true para indicar que se ha manejado la búsqueda
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // No se requiere acción al cambiar el texto de búsqueda
                return false
            }
        })
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
        for (amigo in amigos) {
            val latLng = LatLng((amigo.latitud ?: 0f).toDouble(), (amigo.longitud ?: 0f).toDouble())

            // Aquí se crea el marcador personalizado simulando otro usuario en el mapa
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(amigo.nombre)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            map.addMarker(markerOptions)

        }

        // Zoom y centrado del mapa en la ubicación del otro usuario
        val otherUserLocation = LatLng((amigos[0].latitud ?: 0f).toDouble(), (amigos[0].longitud ?: 0f).toDouble())
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(otherUserLocation, 15f)
        map.animateCamera(cameraUpdate)
    }
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }
    }

    private fun isLocationGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationGranted()) {
            map.isMyLocationEnabled = true

            createLocationRequest()

            val locationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())

            locationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            locationResult ?: return
                            for (location in locationResult.locations) {
                                val latLng = LatLng(location.latitude, location.longitude)
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                                abogado!!.latitud = location.latitude.toFloat()
                                abogado!!.longitud = location.longitude.toFloat()
                                Log.d("Ivan","${ abogado!!.latitud},${abogado!!.longitud }")
                            }
                        }
                    }

                    locationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }
        } else {
            shouldEnableLocation = true
        }
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    if (shouldEnableLocation) {
                        enableLocation()
                        shouldEnableLocation = false
                    } else {
                        // Move camera to current location if permission has already been granted
                        val locationProviderClient =
                            LocationServices.getFusedLocationProviderClient(requireContext())
                        locationProviderClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                val latLng = LatLng(location.latitude, location.longitude)
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ve a ajustes y acepta los permisos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized) return
        if (!isLocationGranted()) {
            map.isMyLocationEnabled = false
            Toast.makeText(
                requireContext(),
                "Ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                })

            val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {

                map.isMyLocationEnabled = true

            }
            task.addOnFailureListener { exception ->

                if (exception is ResolvableApiException) {
                    try {

                        exception.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {

                        Toast.makeText(
                            requireContext(),
                            "Error al intentar mostrar el diálogo de configuración de localización",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    }

