package com.example.movierating.ui.signIn

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInEmailViewModel @Inject constructor(): ViewModel(){
    private val _state = MutableStateFlow<SignInEmailState>(SignInEmailState.Nothing)
    val state = _state.asStateFlow()

    fun signIn(email: String, password: String){
        _state.value = SignInEmailState.Loading
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    saveUserData()
                    _state.value = SignInEmailState.Success
                } else {
                    _state.value = SignInEmailState.Error
                }
            }

    }
}

sealed class SignInEmailState(){
    object Nothing: SignInEmailState()
    object Loading: SignInEmailState()
    object Success: SignInEmailState()
    object Error: SignInEmailState()
}