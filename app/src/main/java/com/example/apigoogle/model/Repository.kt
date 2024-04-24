package com.example.apigoogle.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference

class Repository : ViewModel() {
    private val dataBase = FirebaseFirestore.getInstance()
    fun addMarker(marcador: Marcador) {
        dataBase.collection("markers")
            .add(
                hashMapOf(
                    "owner" to marcador.owner,
                    "id" to marcador.id,
                    "nom" to marcador.nom,
                    "descripcio" to marcador.descripcio,
                    "latitut" to marcador.latitut,
                    "longitud" to marcador.longitud,
                    "tipus" to marcador.tipus
                )
            )
    }

    fun editMarker(editedMarker: Marcador) {
        dataBase.collection("markers").document(editedMarker.id).set(
            hashMapOf(
                "owner" to editedMarker.owner,
                "id" to editedMarker.id,
                "nom" to editedMarker.nom,
                "descripcio" to editedMarker.descripcio,
                "latitut" to editedMarker.latitut,
                "longitud" to editedMarker.longitud,
                "tipus" to editedMarker.tipus
            )
        )

    }

    fun deleteMarker(marker: Marcador) {
        dataBase.collection("markers").whereEqualTo("id", marker.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    dataBase.collection("markers").document(document.id).delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }


    fun getMarkers(): CollectionReference {
        return dataBase.collection("markers")
    }
}