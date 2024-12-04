package com.example.movierating.ui.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.StarRating
import com.example.movierating.data.Movie
import com.example.movierating.data.MovieRated
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun RatingTab() {
    val movies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val movieRated = remember { mutableStateOf<List<MovieRated>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val showComments = remember { mutableStateOf(true) } // 코멘트 표시 여부입니다
    val coroutineScope = rememberCoroutineScope()

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val userData = remember { mutableStateOf<UserData?>(null) }

    LaunchedEffect(user) {
        user?.let {
            val userId = it.uid
            // Firestore에서 해당 userId로 문서 조회
            db.collection("user")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // 첫 번째 문서를 UserData 객체로 변환
                        val document = documents.documents.first()
                        userData.value = document.toObject(UserData::class.java)
                    } else {
                        Log.d("Firestore", "No matching user found")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error querying user data", e)
                }
        }
    }

    // 데이터 로드: Firestore에서 영화 데이터를 가져옴
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedMovies = fetchMoviesFromFirestore() // Firestore에서 영화 목록 가져오기
            val fetchMovieRated = fetchMovieRatedFromFirestore()
            movies.value = fetchedMovies
            movieRated.value = fetchMovieRated
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
                items(movieRated.value) { movieRated ->
                    // 별점 상태를 별도로 관리
                    var rating by remember { mutableStateOf(0f) }
                    if (movieRated.userId == userData.value?.userId) {
                        // movies 에서 movie의 문서명이 movieRated 의 movie 값과 같은 movie 객체를 받아와 하단의 movieCard에 매개변수로 전달하려 해
                        val matchedMovie = movies.value.find { it.DOCID == movieRated.movie }
                        matchedMovie?.let { movie ->
                            MovieCard(
                                movie = movie,
                                rating = movieRated.score?.toFloat() ?: 0f,
                                showComments = showComments.value, // 코멘트 표시 여부 전달
                                onRatingChanged = { newRating ->
                                    rating = newRating
                                },
                                comment = movieRated.comment
                            )
                        }
                    }
                }
            }
        }
    }
}

// 영화 포스터, 별점, 코멘트가 담긴 카드
@Composable
fun MovieCard(
    movie: Movie,
    rating: Float,
    showComments: Boolean,
    onRatingChanged: (Float) -> Unit,
    comment: String?
) {
    var isExpanded by remember { mutableStateOf(false) } // 코멘트 확장 여부 상태

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(Color(0xFFF9F9F9)), // 배경색 연한 그레이
        shape = RoundedCornerShape(10.dp) // 모서리 둥글게
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MovieImage(imageUrl = movie.posters ?: "")

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    Text(
                        text = "${movie.runtime}분 · ${movie.directors.joinToString(", ")} · ${movie.nation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray, // 텍스트 색상 연한 회색
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    // 별점 영역
                    Spacer(modifier = Modifier.height(8.dp))
                    StarRating(
                        initialRating = rating,
                        onRatingChanged = { newRating -> onRatingChanged(newRating) }
                    )
                }
            }

            if (showComments) {
                // 코멘트 영역
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isExpanded) Color(0xFFFFF9E0) else Color(0xFFF7F7F7), // 확장 여부에 따라 배경색 변경
                            RoundedCornerShape(8.dp) // 코멘트 박스도 모서리를 둥글게
                        )
                        .clickable { isExpanded = !isExpanded } // 클릭 시 확장/축소 전환
                        .padding(12.dp) // 코멘트 섹션 패딩
                ) {
                    Text(
                        //text = "코멘트가 없습니다. 진짜 쓸 말이 없어서 그래요. 제가 LA에 있을때 먹었던 돼지국밥처럼 정말 말이 안 나오는 그런 상황이에요. 뭐라고 더 써야 세 줄 이상 나올까요? 생각해보니 줄바꿈을 하면 되는군요. 하하 이왕 썼는데 지우기도 아쉽고 그냥 자고싶고 막 그런데 그냥 그냥 편하게 편하게 살고 싶어요 하하하....",
                        text = comment.toString(),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3, // 확장 여부에 따라 표시 줄 수 변경
                        overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis, // 말줄임표
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

