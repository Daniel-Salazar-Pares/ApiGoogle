package com.example.apigoogle.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apigoogle.model.Authenticaion
import com.example.apigoogle.model.Repository
import com.example.apigoogle.model.Marcador
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MapViewModel : ViewModel() {

    private var _currentUser = MutableLiveData<String>()
    private val currentUser = _currentUser

    fun getCurrentUser(): String? {
        return currentUser.value
    }
    private val database = FirebaseFirestore.getInstance()

    fun setCurrentUser() {
        _currentUser.value = Authenticaion().getUID()!!
    }

    val icons = listOf(
        "baseline_park_24",
        "baseline_school_24",
        "baseline_restaurant_24",
        "baseline_shopping_cart_24"
    )

    val screen = mutableStateOf("Unknown")
    val repository = Repository()

    fun setScreen(screenName: String) {
        screen.value = screenName
    }


    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }


    private val _markers = MutableLiveData<MutableList<Marcador>>()
    val markers: LiveData<MutableList<Marcador>> = _markers

    fun addMarker(id: String, nom: String, descripcio: String, cordenades: LatLng, tipus: String) {
        val newMarker = Marcador(id, nom, descripcio, cordenades.latitude, cordenades.longitude, tipus)
        repository.addMarker(newMarker)
        val tempList = _markers.value?.toMutableList() ?: mutableListOf()
        tempList.add(newMarker)
        _markers.value = tempList

    }


    fun deleteMarker(marker: Marcador) {
        repository.deleteMarker(marker)
        val tempList = _markers.value?.toMutableList() ?: mutableListOf()
        tempList.remove(marker)
        _markers.value = tempList
    }


    fun getMarkers() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            repository.getMarkers()
                .whereEqualTo("owner", _currentUser.value)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("Firestore error", error.message.toString())
                        return@addSnapshotListener
                    }
                    val tempList = mutableListOf<Marcador>()
                    for(dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val document = dc.document
                            val id = document.getString("id") ?: ""
                            val nom = document.getString("nom") ?: ""
                            val descripcio = document.getString("descripcio") ?: ""
                            var latitut: Double = document.getDouble("latitut") ?: 0.0
                            var longitud:Double =  document.getDouble("longitud") ?: 0.0
                            val tipus = document.getString("tipus") ?: ""
                            val owner = document.getString("owner") ?: ""

                            val nouMarker = Marcador(
                                id,
                                nom,
                                descripcio,
                                latitut,
                                longitud,
                                tipus,
                                owner
                            )

                            tempList.add(nouMarker)
                        }
                    }
                    _markers.value = tempList
                }
        } else {
            _markers.value = mutableListOf()
        }
    }
}