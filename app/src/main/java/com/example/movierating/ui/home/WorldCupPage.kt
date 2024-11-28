package com.example.movierating.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupPage (
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "영화 월드컵") },
//                actions = {
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "back")
//                    }
//                }
//            )
//        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "16강")
            }
        }
    }
}