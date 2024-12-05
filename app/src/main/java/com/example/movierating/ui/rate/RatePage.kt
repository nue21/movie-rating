package com.example.movierating.ui.rate
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.example.movierating.data.MovieRated
import com.example.movierating.data.User
import com.example.movierating.service.UserService
import com.example.movierating.ui.profile.MovieCard
import com.example.movierating.ui.profile.RatingTab
import com.example.movierating.ui.profile.fetchMovieRatedFromFirestore
import com.example.movierating.ui.profile.fetchMoviesFromFirestore
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatePage(
    modifier: Modifier = Modifier
) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val userData = remember { mutableStateOf<UserData?>(null) }

    user?.let {
        val userId = it.uid
        db.collection("user").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        userData.value = document.toObject(UserData::class.java)
                    }
                } else {
                    Log.d("Firestore", "No matching user found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error querying user data", e)
            }
    }

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
                text = userData.value?.movieRatedList?.size.toString(), // 별점을 부여하면 곧바로 이 값이 바뀌게끔 구현하고 싶어
                style = typography.displayMedium
            )
            Text(
                text = "평가한 영화 수",
                style = typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(bottom = 8.dp))
            RateRandomTab()
        }
    }
}

@Composable
fun RateRandomTab(){
    val movies = remember { mutableStateOf<List<Movie>>(emptyList()) }
    val movieRated = remember { mutableStateOf<List<MovieRated>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val showComments = remember { mutableStateOf(false) } // 코멘트 표시 여부입니다
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
            val fetchedMovieRated = fetchMovieRatedFromFirestore()
            movies.value = fetchedMovies
            movieRated.value = fetchedMovieRated
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
            // 영화 목록 표시
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(movies.value) { movie ->
                    // 이미 평가한 영화인지 확인
                    val isMovieRated = movieRated.value.any { it.movie == movie.DOCID }

                    // 영화가 이미 평가된 경우 MovieCard를 표시하지 않음
                    if (!isMovieRated) {
                        // 별점 상태를 별도로 관리
                        var rating by remember { mutableStateOf(0f) }
                        MovieCard(
                            movie = movie,
                            rating = rating,
                            showComments = showComments.value, // 코멘트 표시 여부 전달
                            onRatingChanged = { newRating ->
                                rating = newRating
                                saveRating(movie, newRating, userData.value, db)
                            },
                            comment = ""
                        )
                    }
                }
            }
        }
    }
}

private fun saveRating(movie: Movie, score: Float, userData: UserData?, db: FirebaseFirestore) {
    // Firestore 컬렉션 이름
    val movieRatedCollection = db.collection("movieRated")

    // 1. 해당 영화와 사용자 ID에 대한 MovieRated 문서가 있는지 확인
    movieRatedCollection
        .whereEqualTo("movie", movie.DOCID)
        .whereEqualTo("userId", userData?.userId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                // 기존 문서가 있는 경우
                val existingDoc = documents.documents.first()
                val updatedData = mapOf(
                    "score" to score.toDouble(),
                    "updatedTime" to Timestamp.now()
                )
                // 기존 문서 업데이트
                movieRatedCollection.document(existingDoc.id).update(updatedData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "MovieRated updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error updating MovieRated", e)
                    }
            } else {
                // 2. 기존 문서가 없는 경우 새 문서 생성
                val movieRated = userData?.let {
                    MovieRated(
                        movie = movie.DOCID,
                        score = score.toDouble(),
                        comment = "",
                        updatedTime = Timestamp.now(),
                        userId = it.userId
                    )
                }

                if (movieRated != null) {
                    movieRatedCollection.add(movieRated.toMap())
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firestore", "MovieRated saved with ID: ${documentReference.id}")

                            // UserData의 movieRatedList 업데이트
                            val userDocRef = db.collection("user").document(userData?.userId ?: "")

                            // 기존 User 데이터를 가져와서 movieRatedList 업데이트
                            userDocRef.get()
                                .addOnSuccessListener { userDocument ->
                                    if (userDocument.exists()) {
                                        val currentMovieRatedList = userDocument.get("movieRatedList") as? List<String> ?: mutableListOf()

                                        // 기존 리스트에 새 document id 추가
                                        val updatedMovieRatedList = currentMovieRatedList.toMutableList()
                                        updatedMovieRatedList.add(documentReference.id)

                                        // User의 movieRatedList 업데이트
                                        val userUpdate = mapOf("movieRatedList" to updatedMovieRatedList)
                                        userDocRef.update(userUpdate)
                                            .addOnSuccessListener {
                                                Log.d("Firestore", "UserData updated successfully")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Firestore", "Error updating UserData", e)
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error fetching user data", e)
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error saving MovieRated", e)
                        }
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error querying MovieRated", e)
        }
}


