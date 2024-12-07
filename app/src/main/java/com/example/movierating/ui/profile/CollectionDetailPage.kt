package com.example.movierating.ui.profile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movierating.data.Collections
import com.example.movierating.data.Movie
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun CollectionDetailPage(
    modifier: Modifier = Modifier, // 기본값 추가
    navController: NavController,
    collectionId: String?
) {
    val movies = remember { mutableStateOf<MutableList<Movie>>(mutableListOf()) }
    val isLoading = remember { mutableStateOf(true) }
    val showComments = remember { mutableStateOf(false) } // 코멘트 표시 여부입니다
    val coroutineScope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val userData = remember { mutableStateOf<UserData?>(null) }
    val collectionData = remember { mutableStateOf<Collections?>(null) }

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
    LaunchedEffect(collectionId) {
        collectionId?.let {
            // Firestore에서 collectionId로 컬렉션 문서 조회
            db.collection("collections") // "Collections" 컬렉션에서
                .document(collectionId) // collectionId에 해당하는 문서
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // 문서가 존재하면 데이터를 Collections 클래스 객체로 변환
                        collectionData.value = document.toObject(Collections::class.java)
                        // 컬렉션 데이터가 로드되었으므로 영화 ID 목록을 가져와서 영화 목록 로딩 시작
                        collectionData.value?.movieList?.let { movieIds ->
                            val totalMovies = movieIds.size
                            var loadedMovies = 0
                            movieIds.forEach { movieId ->
                                db.collection("movies") // "movies" 컬렉션에서
                                    .document(movieId) // 각 movieId에 해당하는 문서
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            // 영화 데이터를 Movie 객체로 변환하여 movies에 추가
                                            val movie = document.toObject(Movie::class.java)
                                            movie?.let {
                                                // MutableList에 직접 추가
                                                movies.value.add(it)
                                                // 상태 업데이트 (새 리스트로 변경)
                                                movies.value = movies.value.toMutableList()
                                            }
                                        } else {
                                            Log.d("Firestore", "No matching movie found for ID: $movieId")
                                        }
                                        loadedMovies++
                                        if (loadedMovies == totalMovies) {
                                            // 모든 영화가 로드된 후에 isLoading을 false로 설정
                                            isLoading.value = false
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error querying movie data", e)
                                        loadedMovies++
                                        if (loadedMovies == totalMovies) {
                                            // 모든 영화가 로드된 후에 isLoading을 false로 설정
                                            isLoading.value = false
                                        }
                                    }
                            }
                        }
                    } else {
                        Log.d("Firestore", "No matching collection found")
                        isLoading.value = false
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error querying collection data", e)
                    isLoading.value = false
                }
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
                        },
                        comment = ""
                    )
                }
            }
        }
    }
}