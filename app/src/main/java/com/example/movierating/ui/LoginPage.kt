package com.example.movierating.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.movierating.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginPage (
    modifier: Modifier = Modifier,
    onClickLogin: () -> Unit
) {


    Column (
        modifier = modifier
            .fillMaxSize()
    ){
        Text(text = "login")
        Button(onClick = {
            onClickLogin()
        }) {
            Text("Sign in with Google")
        }
    }
}