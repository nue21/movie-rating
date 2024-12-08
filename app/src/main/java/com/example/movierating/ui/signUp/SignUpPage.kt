package com.example.movierating.ui.signUp

import android.widget.Toast
import androidx.compose.foundation.Image
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
import com.example.movierating.R

@Composable
fun SignUpPage(navController: NavController){
    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsState()

    var name by remember{
        mutableStateOf("")
    }
    var email by remember{
        mutableStateOf("")
    }
    var password by remember{
        mutableStateOf("")
    }

    var confirm by remember{
        mutableStateOf("")
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.value) {
        when(uiState.value){
            is SignUpState.Success -> {
                navController.navigate("signIn")
            }
            is SignUpState.Error -> {
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
                contentDescription = "chat"
            )
            Spacer(modifier = Modifier.padding(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = {name = it},
                label = {Text(text = "name")},
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.padding(4.dp))
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
            Spacer(modifier = Modifier.padding(4.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = {confirm = it},
                label = {Text(text = "Confirm Password")},
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = password.isNotEmpty() && confirm.isNotEmpty() && password != confirm
            )
            Spacer(modifier = Modifier.padding(16.dp))
            if (uiState.value == SignUpState.Loading) {
                CircularProgressIndicator()
            }
            else {
                Button(
                    onClick = { viewModel.signUp(name, email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirm.isNotEmpty() && confirm == password,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFDCC7FF), // 연보라색 배경
                        contentColor = androidx.compose.ui.graphics.Color(0xFF6200EA)  // 텍스트 색상
                    )
                ) {
                    Text(text = stringResource(R.string.signup))
                }
                TextButton(onClick = {}) {
                    Text(text = stringResource(R.string.signuptext))
                }
            }
        }
    }
}