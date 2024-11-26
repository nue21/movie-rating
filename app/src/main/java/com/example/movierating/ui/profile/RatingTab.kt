package com.example.movierating.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.movierating.data.Movie
import kotlinx.coroutines.launch

@Composable
fun RatingTabContent() {
    val movies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val showComments = remember { mutableStateOf(true) } // 코멘트 표시 여부입니다
    val coroutineScope = rememberCoroutineScope()

    // 데이터 로드: Firestore에서 영화 데이터를 가져옴
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedMovies = fetchMoviesFromFirestore() // Firestore에서 영화 목록 가져오기
            movies.value = fetchedMovies
            isLoading.value = false // 데이터 로드 완료 후 로딩 상태 해제
        }
    }

    // 로딩 상태일 때 로딩 인디케이터 표시
    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // 로딩 중 표시
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // 버튼 섹션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showComments.value = !showComments.value }, // 코멘트 표시 토글
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                ) {
                    Text(
                        text = if (showComments.value) "별점만" else "코멘트 보기",
                    )
                }

                Button(
                    onClick = { /* 정렬 관련 로직 추가 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                ){
                    Text(
                        text = "최근 작성 순",
                    )
                }
            }

            // 영화 목록 표시
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(movies.value) { movie ->
                    // 별점 상태를 별도로 관리
                    var rating by remember { mutableStateOf(0f) }
                    MovieCard(
                        movie = movie,
                        rating = rating,
                        showComments = showComments.value, // 코멘트 표시 여부 전달
                        onRatingChanged = { newRating ->
                            rating = newRating
                        }
                    )
                }
            }
        }
    }
}
