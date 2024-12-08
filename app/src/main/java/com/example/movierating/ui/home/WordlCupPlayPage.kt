package com.example.movierating.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupPlayPage (
    navController: NavController,
    worldCupViewModel: WordlCupViewModel
) {
    val roundList: List<String> = listOf("64강", "32강", "16강", "8강", "준결승", "3,4위", "결승")
    val num = 1 - worldCupViewModel.round.value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .width(240.dp)
                    .padding(24.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFC6767),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color(0xFFFC6767))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = roundList[num + worldCupViewModel.roundCnt.value]+"전",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${worldCupViewModel.gameCnt.value} 경기",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFFC6767)
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MatchItem(
                    movie = worldCupViewModel.currentMathUp.value[0],
                    onClick = {
                        worldCupViewModel.chooseMovie(0)
                        if(worldCupViewModel.state.value == WorldCupState.GameOver){
                            navController.navigate("worldCupResultDetail")
                        }
                    }
                )
                Text(
                    text = "VS",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFC6767)
                    )
                )
                MatchItem(
                    movie = worldCupViewModel.currentMathUp.value[1],
                    onClick = {
                        worldCupViewModel.chooseMovie(1)
                        if(worldCupViewModel.state.value == WorldCupState.GameOver){
                            navController.navigate("worldCupResultDetail")
                        }
                    }
                )
            }

        }
    }
}

@Composable
fun MatchItem (
    movie: WorldCupMovie,
    onClick : () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // 수직 정렬
        ) {
            MovieImage(movie.posters)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(227.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = movie.title,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 2
                    )
                    val instant = movie.updatedTime?.toInstant()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
                    Text(
                        text = formatter.format(instant),
                        modifier = Modifier.align(Alignment.End),
                        style = TextStyle(
                            fontSize = 13.sp
                        )
                    )
                }
                Text(
                    text = movie.comment?: "",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(
                        fontSize = 12.sp
                    ),
                    maxLines = 5
                )
            }
        }
    }
}

@Composable
fun MovieImage(imageUrl: String?) {
    val posterWidth = 154.dp
    val posterHeight = 227.dp // 세로로 더 긴 비율 설정 (원래 포스터 비율에 맞춤)

    Surface(
        modifier = Modifier
            .size(width = posterWidth, height = posterHeight) // 고정된 크기 설정
            .clip(RoundedCornerShape(8.dp)), // 이미지 둥근 모서리 처리
        color = Color.LightGray // 이미지가 로드되지 않을 경우 색상
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Movie Thumbnail",
            contentScale = ContentScale.Crop // 이미지 비율 유지하며 채우기
        )
    }
}