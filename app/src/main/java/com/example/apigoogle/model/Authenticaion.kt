package com.example.apigoogle.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class Authenticaion : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private var _goToNext = MutableLiveData<Boolean>(false)
    val goToNext: LiveData<Boolean> = _goToNext
    fun changeGoToNext() {
        _goToNext.value = false
    }

    val _accountCreated = MutableLiveData<Boolean>()
    val accountCreated: LiveData<Boolean> = _accountCreated
    fun changeAccountCreated() {
        _accountCreated.value = false
    }

    private var _singupError = MutableLiveData<Boolean>(false)
    val singupError: LiveData<Boolean> = _singupError
    fun changeSingupError() {
        _singupError.value = false
    }

    private var _loginError = MutableLiveData<Boolean>()
    val loginError: LiveData<Boolean> = _loginError
    fun changeLoginError() {
        _loginError.value = false
    }

    private var _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    var _loggedUser = MutableLiveData<String>()
    val loggedUser: LiveData<String> = _loggedUser
    fun register(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _accountCreated.value = true
                } else {
                    _singupError.value = true
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Log.d("Error", "Email is already in use: ${task.exception?.message}")
                    } else if (task.exception is FirebaseAuthWeakPasswordException) {
                        Log.d("Error", "Password is too short: ${task.exception?.message}")
                    } else {
                        Log.d("Error", "Error al registrar l'usuari: ${task.exception?.message}")
                    }
                }
            }
    }

    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result?.user?.uid
                    _loggedUser.value = task.result?.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                } else {
                    _loginError.value = true
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d("Error", "Invalid credentials: ${task.exception?.message} ")
                    } else {
                        Log.d("Error", "Error al iniciar sessió: ${task.exception?.message} ")
                    }
                }
            }
    }
    fun getUID() = auth.currentUser?.uid
    fun logOut() {
        auth.signOut()
    }

}