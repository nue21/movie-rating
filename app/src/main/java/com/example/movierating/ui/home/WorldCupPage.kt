package com.example.movierating.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movierating.R
import com.example.movierating.ui.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupPage (
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    worldCupViewModel: WordlCupViewModel
) {
    val roundList: List<Int> = listOf(16, 32, 64)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "영화 월드컵") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_worldcup),
                contentDescription = "worldCup",
                modifier = Modifier.size(80.dp, 80.dp),
                contentScale = ContentScale.FillWidth
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                roundList.forEach { round ->
                    ActionButton(
                        btn = round,
                        enable = round < userViewModel.userData.value?.movieRatedList?.size!!,
                        onClick = {
                            worldCupViewModel.setRound(round)
                            worldCupViewModel.fetchData(userViewModel.userData.value?.movieRatedList)
                            navController.navigate("worldCupPlay/$round")
                        })
                }

                ActionButton(btn = null, enable = userViewModel.userData.value?.movieRatedList?.size != 0, onClick = {})
            }
        }
    }
}

@Composable
fun ActionButton (
    btn: Int?,
    enable: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = if (enable) Brush.linearGradient(
                    colors = listOf(Color(0xFFFC6767), Color(0xFFFF947D))
                ) else
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFC2C2C2), Color(0xFFC2C2C2))
                    )
            )
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 8.dp),
    ) {
        Text(
            text = if(btn == null) "이전 기록 보기" else "${btn}강",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MovieRatingTheme {
//        WorldCupPage()
//    }
//}