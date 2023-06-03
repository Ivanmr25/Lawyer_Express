package com.example.lawyerexpress.Fragmentos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MapFragment : Fragment() {
    private lateinit var map: GoogleMap
    private lateinit var viewModel: MainViewModel
    private var currentLocationMarker: Marker? = null
    private lateinit var locationRequest: LocationRequest
    private var locationPermissionGranted = false
    private var shouldEnableLocation = false
    private val REQUEST_LOCATION_PERMISSION = 1
    private val REQUEST_CHECK_SETTINGS = 2
    private var abogado: Abogado? = null
    private lateinit var amigos: List<Abogado>
    private val locationHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Obtener el objeto abogado del fragmento de registro o del inicio de sesion
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
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            map = googleMap

            if (isLocationGranted()) {
                enableLocation()
                map.isMyLocationEnabled = true     // Mostrar la ubicación actual en el mapa
                startLocationUpdates()

                map.setOnMyLocationButtonClickListener {
                    // Centrar la cámara en la ubicación actual
                    val location = map.myLocation
                    location?.let {
                        val currentLocation = LatLng(it.latitude, it.longitude)
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 15f)
                        map.animateCamera(cameraUpdate)
                    }
                    true
                }
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
        GetAmigos()

        //Busqueda de amigo en el caso que haya a partir de la barra de busqueda
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        // Configurar el listener de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Realizar búsqueda en la lista de amigos
                val foundAmigo = amigos.find { it.nombre.contains(query ?: "", true) }

                // Si se encuentra el amigo, centrar la cámara en su ubicación
                if (foundAmigo != null) {
                    val amigoLocation = LatLng((foundAmigo.latitud ?: 0f).toDouble(), (foundAmigo.longitud ?: 0f).toDouble())
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

    //Metodo para conseguir los amigos de ese abogado
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

    //Metodo que a partir de la lista de amigos que se trae de la API coge la ubicacion y la pinta en el mapa
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


    }

    //Metodo encargado de crear la solicitud de la ubicacion junto a los intervalos de actualizacion de la ubicacion
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }
    }
    //Metodo encargado de verificar si se ha dado permisos de ubicacion
    private fun isLocationGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    //Metodo para habilitar la funcion de localizacion en el mapa si los permisos se han concedido
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

    //Metodo que se encarga de manejar la respuesta de la solicitud de los permisos de ubicacion
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

    //Metodo para resumir el fragmento para que te vuelva a salir la localizacion y su funcion de actualizar
    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        GetAmigos()
        locationHandler.post(locationRunnable)

        if (!::map.isInitialized) return

        //Comprobacion en el caso de que hayas desactivado la localizacion en el cual te avisa de que tienes que activar la localizacion
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
    private fun startLocationUpdates() {
        locationHandler.postDelayed(locationRunnable, 10000) // Repetir cada 10 segundos (10000 milisegundos)
    }


    //Runnable que se encarga de realizar el metodo de actualizar la ubicacion cada 10 segundos
    @SuppressLint("MissingPermission")
    private val locationRunnable = object : Runnable {
        override fun run() {
            // Obtener la ubicación actual
            val locationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            locationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude.toFloat()
                    val longitude = location.longitude.toFloat()

                    // Imprimir la latitud y longitud en la consola
                    Log.d("Ubicación", "Latitud: $latitude, Longitud: $longitude")

                    // Actualizar la ubicación en la base de datos
                    viewModel.UpdateLocation(abogado!!.numero_colegiado, latitude, longitude)
                        .observe(viewLifecycleOwner) { it ->
                            it?.let {
                                // Actualizar la posición en el mapa
                                abogado!!.latitud = it.latitud
                                abogado!!.longitud = it.longitud
                                updateMapPosition(abogado!!.latitud!!.toFloat(), abogado!!.longitud!!.toFloat())
                            }
                        }

                    // Volver a programar la obtención de ubicación después de 10 segundos
                    locationHandler.postDelayed(this, 10000) // Repetir cada 10 segundos (10000 milisegundos)
                } else {
                    // Si no se pudo obtener la ubicación, volver a programar la obtención después de 10 segundos
                    locationHandler.postDelayed(this, 10000) // Repetir cada 10 segundos (10000 milisegundos)
                }
            }
        }
    }

    //Metodo que se encarga de conseguir tu ubicacion y llevar la camara a tu posicion
    private fun updateMapPosition(latitude: Float, longitude: Float) {
        if (!::map.isInitialized) return

        val location = LatLng(latitude.toDouble(), longitude.toDouble())

        // Centrar la cámara en la ubicación actualizada
        val cameraUpdate = CameraUpdateFactory.newLatLng(location)
        map.animateCamera(cameraUpdate)

        // Actualizar el marcador de la posición en el mapa
        if (currentLocationMarker != null) {
            currentLocationMarker!!.remove()
        }

        val markerOptions = MarkerOptions()
            .position(location)
            .title("Mi ubicación")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        currentLocationMarker = map.addMarker(markerOptions)
        GetAmigos()
    }


    // Detener el Runnable cuando el fragmento se pausa
    override fun onPause() {
        super.onPause()

        locationHandler.removeCallbacks(locationRunnable)
    }

    }

