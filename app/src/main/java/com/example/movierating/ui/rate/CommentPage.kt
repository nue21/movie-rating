package com.example.movierating.ui.rate

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movierating.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentPage(
    modifier: Modifier = Modifier
){
    val movies = listOf(
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/55/tn_DPK022735.jpg", "오드리", "2024 · 1시간 15분", "오드리 재밌다 (아직 안봄)"),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/51/tn_DPF029805.jpg", "프로이드의 라스트 세션", "2024 · 1시간 15분", "에고 이드 초에고 프로이트 화이팅"),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/50/tn_DPF029783.jpg", "10 라이브즈", "2024 · 1시간 15분", "나도 목숨 10개 가지고 싶다. 하나 주라."),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/46/tn_DPF029705.jpg", "굿 바이 크루얼 월드", "2024 · 1시간 15분", "잔인한 세상 미워\n행복했으면 좋겠다."),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/43/tn_DPF029352.jpg", "키타로 탄생", "2024 · 1시간 15분", "복숭아동자 리메이크"),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/37/tn_DPF029035.jpg", "사랑은 비", "2024 · 1시간 15분", "가뭄")
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {},
                navigationIcon = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "List"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "평점과 코멘트",
                style = typography.displayMedium,
                fontSize = 30.sp
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(movies) { movie ->
                    MovieCommentCard(
                        moviePosterUrl = movie.posterUrl,
                        movieTitle = movie.title,
                        movieInfo = movie.info,
                        movieComment = movie.comment
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCommentCard(
    moviePosterUrl: String,
    movieTitle: String,
    movieInfo: String,
    movieComment: String,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        Row {
            MovieImage(imageUrl = moviePosterUrl)

            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = movieTitle,
                        style = typography.titleLarge,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_emptystar), // 별 모양 아이콘 리소스
                                contentDescription = "Star Icon",
                                tint = Color.Yellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "2.5",
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }


                }
                Text(
                    text = movieInfo,
                    style = typography.titleMedium,
                    fontSize = 12.sp
                )
                Text(
                    text = movieComment,
                    style = typography.titleMedium,
                    fontSize = 18.sp
                )
            }
        }
    }
}


