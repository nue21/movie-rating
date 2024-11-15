package com.example.movierating.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfilePage (
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(Color.LightGray), //실제 이미지 넣을 때 수정 필요
            contentAlignment = Alignment.TopEnd
        ){
            Button(
                onClick = {},
                modifier = Modifier.padding(8.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "수정", color = Color.Black)
            }
            Surface(
                modifier = Modifier.size(100.dp).align(Alignment.BottomCenter),
                shape = CircleShape,
                color = Color.Gray
            ) {  }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "닉네임",
            fontSize = 24.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().height(1.dp),
            color = Color.Gray
        ){  }

        Spacer(modifier = Modifier.height(20.dp))
        
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Button(onClick = {}) { Text(text = "평가") }
            Button(onClick = {navController.navigate("like")}) { Text(text = "보고싶어요") }
            Button(onClick = {}) { Text(text = "컬렉션") }
        }
    }
}