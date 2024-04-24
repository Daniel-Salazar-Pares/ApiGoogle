package com.example.apigoogle.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apigoogle.model.Authentication
import com.example.apigoogle.model.Repository
import com.example.apigoogle.model.Marcador
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MapViewModel: ViewModel() {
    //General utility functions and variables
    val screen = mutableStateOf("Unknown")
    fun setScreen(screenName: String) {
        screen.value = screenName
    }

    val icons = listOf(
        "baseline_park_24",
        "baseline_school_24",
        "baseline_restaurant_24",
        "baseline_shopping_cart_24"
    )

    //Functions and variables related to the Authentication
    private var _currentUser = MutableLiveData<String>()
    private val currentUser = _currentUser
    fun getCurrentUser(): String? {
        return currentUser.value
    }

    fun setCurrentUser() {
        _currentUser.value = Authentication().getUID()!!
    }

    //Functions and variables related to the DataBase
    val repository = Repository()

    val _markers =  MutableLiveData<MutableList<Marcador>>()
    val markers: MutableLiveData<MutableList<Marcador>> = _markers

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
                    for (doc in value!!) {
                        val marcador = doc.toObject(Marcador::class.java)
                        tempList.add(marcador)
                    }
                    _markers.value = tempList
                }
        } else {
            _markers.value = mutableListOf()
        }
    }

    fun getMarkerById(id: String): Marcador? {
        return markers.value?.find { it.id == id }
    }

    fun addMarker(id: String, nom: String, descripcio: String, cordenades: LatLng, tipus: String) {
        val newMarker =
            Marcador(id, nom, descripcio, cordenades.latitude, cordenades.longitude, tipus)
        repository.addMarker(newMarker)
    }

    fun deleteMarker(marker: Marcador) {
        repository.deleteMarker(marker)
    }

    fun editMarker(marker: Marcador) {
        repository.editMarker(marker)
    }


}
