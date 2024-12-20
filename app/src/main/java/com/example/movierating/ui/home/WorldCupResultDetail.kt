package com.example.movierating.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupResultDetail (
    navController: NavController,
    wordlCupViewModel: WordlCupViewModel
) {
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "영화 월드컵") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("worldCup"){
                                popUpTo("worldCup") {
                                    inclusive = false
                                }
                            }
                    }) {
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
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .padding(24.dp)
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
                    text = "전체 결과",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFFFC6767)
                    )
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                itemsIndexed(wordlCupViewModel.worldCupResult.value) { index, item: WorldCupMovie ->
                    WorldCupResultItem(item, index)
                }
            }
            TextButton(
                onClick = { navController.navigate("worldCup"){
                    popUpTo("worldCup") {
                        inclusive = false
                    }
                } },
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
                Text(text = "다시하기")
            }
        }
    }
}

@Composable
fun WorldCupResultItem (
    item: WorldCupMovie,
    index: Int
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier
                .width(82.dp)
                .height(121.dp)
                .clip(RoundedCornerShape(8.dp)), // 이미지 둥근 모서리 처리
            color = Color.LightGray // 이미지가 로드되지 않을 경우 색상
        ) {
            AsyncImage(
                model = item.posters,
                contentDescription = "Movie Thumbnail",
                contentScale = ContentScale.Crop // 이미지 비율 유지하며 채우기
            )
        }

        Text(text = "${index+1}", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFC6767)))
        Text(text = item.title, style = TextStyle(fontSize = 18.sp))
    }
}