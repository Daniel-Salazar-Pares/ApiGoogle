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
        // Querying the database for documents where 'nom' equals the specified marker name
        dataBase.collection("markers")
            .whereEqualTo("id", editedMarker.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Updating each document that matches the query
                    dataBase.collection("markers").document(document.id).update(
                        mapOf(
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
            }
            .addOnFailureListener { e ->
                // Handle the case where the fetch fails
                Log.w("DatabaseError", "Error updating document", e)
            }
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