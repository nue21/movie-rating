package com.example.movierating.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavController
import com.example.movierating.data.Movie
import kotlinx.coroutines.launch

@Composable
fun SelectedCollection(
    modifier: Modifier = Modifier, // 기본값 추가
    navController: NavController
) {
    val movies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val showComments = remember { mutableStateOf(false) } // 코멘트 표시 여부입니다
    val coroutineScope = rememberCoroutineScope()

    // 데이터 로드: Firestore에서 영화 데이터를 가져옴
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedMovies = fetchMoviesFromFirestore() // Firestore에서 영화 목록 가져오기
            movies.value = fetchedMovies
            isLoading.value = false // 데이터 로드 완료 후 로딩 상태 해제
        }
    }

    if (isLoading.value) {
        // 로딩 상태일 때 로딩 인디케이터 표시
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // 로딩 중 표시
        }
    } else {
        // 전체 레이아웃을 하나의 Column으로 통합
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // 전체 패딩
        ) {
            // 상단바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp), // 섹션 간 간격 추가
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) { // 뒤로가기 버튼
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "뒤로가기"
                    )
                }
                Text(text = "컬렉션 이름", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { /* 수정 동작 */ }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "컬렉션 편집"
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