package com.example.movierating.ui.movieInfo

import android.util.Log
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movierating.data.Movie
import com.example.movierating.data.MovieRated
import com.example.movierating.ui.profile.MovieCard
import com.example.movierating.ui.profile.fetchMovieRatedFromFirestore
import com.example.movierating.ui.profile.fetchMoviesFromFirestore
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.format.TextStyle

@Composable
fun AddCommentPage(
    navController: NavController,
    modifier: Modifier,
    docId: String?
){
    val currentUser = FirebaseAuth.getInstance().currentUser
    var originalRating by remember { mutableStateOf(0f) } // 기존 별점 저장
    var rating by remember { mutableStateOf(0f) } // 현재 별점 저장
    var movie by remember { mutableStateOf<Movie?>(null) }
    var comment by remember { mutableStateOf("") }

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
            IconButton(onClick = {
                movie?.let {
                    saveRatingAndComment(
                        movie = it,
                        currentRating = rating,
                        originalRating = originalRating,
                        currentUser = currentUser,
                        comment = comment
                    )
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "편집완료"
                )
            }
        }
        MovieInfoTab(
            docId = docId,
            onMovieLoaded = { loadedMovie ->
                movie = loadedMovie
            },
            onRatingLoaded = { loadedRating ->
                originalRating = loadedRating // Firestore에서 불러온 기존 별점 저장
                rating = loadedRating // 현재 별점을 기존 값으로 초기화
            },
            onRatingChanged = { newRating ->
                rating = newRating // 사용자 입력 시 별점 상태 업데이트
            }
        )
        CommentTab(
            comment = comment,
            onCommentChanged = { newComment ->
                comment = newComment // 작성된 코멘트 저장
            })

    }
}

@Composable
fun MovieInfoTab(
    docId: String?,
    onMovieLoaded: (Movie) -> Unit,
    onRatingLoaded: (Float) -> Unit, // 기존 별점 전달
    onRatingChanged: (Float) -> Unit // 별점 변경시 호출
){
    val isLoading = remember { mutableStateOf(true) }
    val movie = remember { mutableStateOf<Movie?>(null) }
    val user = FirebaseAuth.getInstance().currentUser
    var userRating by rememberSaveable { mutableStateOf(0f) }
    var comment by rememberSaveable { mutableStateOf("") }

    // 1. 영화 정보 불러오기
    LaunchedEffect(docId) {
        docId?.let {
            FirebaseFirestore.getInstance().collection("movies")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val loadedMovie = document.toObject(Movie::class.java)
                        loadedMovie?.let{
                            movie.value = it
                            onMovieLoaded(it)
                        }
                    }
                    isLoading.value = false
                }
                .addOnFailureListener {
                    isLoading.value = false
                }
        }
    }
    // 2. 사용자 별점 및 코멘트 불러오기
    LaunchedEffect(docId) {
        user?.let {
            val (fetchedRating, fetchedComment) = fetchUserRatingAndComment(it.uid, docId)
            userRating = fetchedRating // 사용자 별점 업데이트
            comment = fetchedComment // 사용자 코멘트 업데이트
            onRatingLoaded(fetchedRating) // 기존 별점 전달
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
    }
    else {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 영화 목록 표시
            movie.value?.let { movie ->
                MovieCard(
                    movie = movie,
                    rating = userRating, // 사용자 별점 초기화
                    showComments = false,
                    onRatingChanged = { newRating ->
                        userRating = newRating // 별점 상태 업데이트
                        onRatingChanged(newRating)
                    },
                    comment = comment
                )
            }
        }
    }
}

@Composable
fun CommentTab(
    comment: String,
    onCommentChanged: (String) -> Unit
){
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = comment,
            onValueChange = onCommentChanged,
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

fun saveRatingAndComment(
    movie: Movie,
    currentRating: Float,
    originalRating: Float,
    currentUser: FirebaseUser?,
    comment: String
){
    currentUser?.let {
        val firestore = FirebaseFirestore.getInstance()
        val userId = it.uid
        val userCollection = firestore.collection("user").document(userId)

        if (comment.isNotEmpty()) {
            firestore.collection("movieRated")
                .whereEqualTo("movie", movie.DOCID)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    // 기존 문서가 있으면 업데이트
                    if (!documents.isEmpty) {
                        // 별점이 새로 부여됐다면 업데이트
                        if(currentRating != originalRating){
                            val existingDoc = documents.documents.first()
                            firestore.collection("movieRated")
                                .document(existingDoc.id)
                                .update(
                                    "score", currentRating,
                                    "comment", comment, // comment 필드 추가
                                    "updatedTime", Timestamp.now()
                                )
                        }
                        else{
                            val existingDoc = documents.documents.first()
                            firestore.collection("movieRated")
                                .document(existingDoc.id)
                                .update(
                                    "comment", comment, // comment 필드 추가
                                    "updatedTime", Timestamp.now()
                                )
                        }
                    }
                    else{
                        // 새 문서 생성
                        val newRating = hashMapOf(
                            "movie" to movie.DOCID,
                            "score" to currentRating,
                            "comment" to comment, // comment 필드 추가
                            "userId" to userId,
                            "updatedTime" to Timestamp.now()
                        )
                        firestore.collection("movieRated").add(newRating)
                            .addOnSuccessListener { documentReference ->
                                val documentId = documentReference.id // 문서 ID 가져오기
                                // user의 movieRatedList 업데이트
                                userCollection.update("movieRatedList", FieldValue.arrayUnion(documentId))
                                    .addOnSuccessListener {
                                        println("movieRatedList updated successfully")
                                    }
                                    .addOnFailureListener { e ->
                                        println("Failed to update movieRatedList: ${e.message}")
                                    }
                            }
                            .addOnFailureListener { e ->
                                println("Failed to add new rating: ${e.message}")
                            }
                    }
                }
        }
        /*
        // 기존 별점과 다를 경우만 Firestore에 업데이트
        if (currentRating != originalRating || comment.isNotEmpty()) {
            val data = mapOf(
                "movie" to movie.DOCID,
                "userId" to user.uid,
                "score" to currentRating,
                "comment" to comment,
                "updatedTime" to Timestamp.now()
            )

            firestore.collection("movieRated")
                .document("${user.uid}_${movie.DOCID}")
                .set(data)
        }
    */
    }
}

// firestore에서 별점과 코멘트를 불러오는 함수
suspend fun fetchUserRatingAndComment(userId: String, movieDocId: String?): Pair<Float, String> {
    return try {
        movieDocId?.let {
            // 1. user의 movieRatedList 확인
            val userSnapshot = FirebaseFirestore.getInstance()
                .collection("user")
                .document(userId)
                .get()
                .await()

            val ratedMovieIds = userSnapshot.get("movieRatedList") as? List<String> ?: emptyList()

            // 2. user의 movieRatedList에서 일치하는 문서ID 확인
            val matchedDocId = ratedMovieIds.firstOrNull { ratedMovieId ->
                val ratedDoc = FirebaseFirestore.getInstance()
                    .collection("movieRated")
                    .document(ratedMovieId)
                    .get()
                    .await()

                ratedDoc.getString("movies") == movieDocId // 영화 DOCID와 일치하는지 비교
            }

            // 3. 별점 및 코멘트 불러오기
            matchedDocId?.let {
                val ratingSnapshot = FirebaseFirestore.getInstance()
                    .collection("movieRated")
                    .document(it)
                    .get()
                    .await()

                val score = ratingSnapshot.getDouble("score")?.toFloat() ?: 0f
                val comment = ratingSnapshot.getString("comment") ?: ""

                Pair(score, comment) // 별점과 코멘트를 반환
            } ?: Pair(0f, "") // 일치하는 문서가 없을 경우 기본값 반환
        } ?: Pair(0f, "") // `movieDocId`가 null일 경우 기본값 반환
    } catch (e: Exception) {
        Pair(0f, "") // 에러 발생 시 기본값 반환
    }
}
