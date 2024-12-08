package com.example.movierating.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movierating.ui.theme.MovieRatingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupResultPage(
    navController: NavController,
     wordlCupViewModel: WordlCupViewModel
) {
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
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFC6767),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "최종 결과",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFFFC6767)
                    )
                )
            }
            Row (
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                ChartColumn(2, 98, 145)
                ChartColumn(1, 110, 162)
                ChartColumn(3, 86, 127)
            }
            TextButton(
                onClick = { navController.navigate("worldCupResultDetail") },
                modifier = Modifier
                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2 // 아래쪽 위치 계산
                        drawLine(
                            color = Color.Black,
                            start = Offset(0f, y), // 왼쪽 아래 시작
                            end = Offset(size.width, y), // 오른쪽 아래 끝
                            strokeWidth = strokeWidth
                        )
                    }
            ) {
                Text(text = "전체 결과 보기")
            }
        }
    }
}

@Composable
fun ChartColumn (
    rank: Int,
    width: Int,
    height: Int
) {
    val imageUrl = "https://i.namu.wiki/i/OEuOP57u456d7WaUNIny-oq820mHPlEijM7_UaOJhvWknulqj5ROlU4_ilC-UPimSXPOM8FevymOZvuhBBzrpg.webp"
    Column (
        modifier = Modifier.width(width.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 글자
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text= "${rank}위",
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFC6767)
                )
            )
            Text(
                text= "타이타닉",
                style = TextStyle(
                    fontSize = 18.sp
                )
            )
        }

        // 사진
        Surface(
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp)), // 이미지 둥근 모서리 처리
            color = Color.LightGray // 이미지가 로드되지 않을 경우 색상
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Movie Thumbnail",
                contentScale = ContentScale.Crop // 이미지 비율 유지하며 채우기
            )
        }
        // 단상
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((140 + (3 - rank) * 30).dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(Color(0xFFFF947D))
        )
    }
}