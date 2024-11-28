package com.example.movierating.ui.movieInfo

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.movierating.ui.profile.MovieCard
import com.example.movierating.ui.profile.fetchMoviesFromFirestore
import kotlinx.coroutines.launch
import java.time.format.TextStyle

@Composable
fun AddCommentPage(
    navController: NavController,
    modifier: Modifier
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ExitToApp,
                    contentDescription = "뒤로가기"
                )
            }
            Text(text = "코멘트", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { /* 수정 동작 */ }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "컬렉션 편집"
                )
            }
        }
        MovieInfoTab()
        CommentTab()

    }
}

@Composable
fun MovieInfoTab(){
    val movies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
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
        Column(modifier = Modifier.fillMaxWidth()) {
            // 영화 목록 표시
            Column(modifier = Modifier.fillMaxWidth()) {
                    // 별점 상태를 별도로 관리
                var rating by remember { mutableStateOf(0f) }
                MovieCard(
                    movie = movies.value.get(0),
                    rating = rating,
                    showComments = false, // 코멘트 표시 여부 전달
                    onRatingChanged = { newRating ->
                        rating = newRating
                    }
                )
            }
        }
    }
}

@Composable
fun CommentTab(){
    var comment by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("코멘트 작성") },
            modifier = Modifier
                .fillMaxWidth()   // 가로로 전체 크기 채우기
                .height(200.dp)   // 높이 조절 (크게 만들기)
                .padding(16.dp),  // 여백 추가
            keyboardActions = KeyboardActions(
                onDone = {
                    // Done 버튼을 눌렀을 때의 처리
                }
            ),
            singleLine = false  // 여러 줄 입력을 허용
        )
    }
}