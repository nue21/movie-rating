package com.example.movierating.ui.signIn

import com.example.movierating.R


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SignInEmailPage(navController: NavController){
    val viewModel: SignInEmailViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()

    var email by remember{
        mutableStateOf("")
    }
    var password by remember{
        mutableStateOf("")
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.value) {
        when(uiState.value){
            is SignInEmailState.Success -> {
                navController.navigate("main"){
                    popUpTo("signIn") { inclusive = true }
                }
            }
            is SignInEmailState.Error -> {
                Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT)
            }
            else -> {}
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize()){ paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(R.drawable.appicon),
                contentDescription = "appicon"
            )
            Spacer(modifier = Modifier.padding(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = {Text(text = "Email")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(4.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = {Text(text = "Password")},
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.padding(16.dp))
            if (uiState.value == SignInEmailState.Loading) {
                CircularProgressIndicator()
            }
            else {
                Button(
                    onClick = { viewModel.signIn(email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFDCC7FF), // 연보라색 배경
                        contentColor = androidx.compose.ui.graphics.Color(0xFF6200EA)  // 텍스트 색상
                    )
                ) {
                    Text(text = stringResource(R.string.signin))
                }
                TextButton(onClick = { navController.navigate("signUp")}) {
                    Text(text = stringResource(R.string.signintext))
                }
            }
        }
    }
}