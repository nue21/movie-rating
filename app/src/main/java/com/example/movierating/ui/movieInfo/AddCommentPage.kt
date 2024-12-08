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
import com.google.firebase.firestore.auth.User
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
    var originalComment by remember { mutableStateOf("") } // 기존 코멘트 저장
    var comment by remember { mutableStateOf("") } // 현재 코멘트 저장

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
                        currentComment = comment,
                        originalComment = originalComment
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
            },
            onCommentLoaded = { loadedComment ->
                originalComment = loadedComment // Firestore에서 불러온 기존 코멘트 저장
                comment = loadedComment // 현재 코멘트를 기존 값으로 초기화
            },
        )
        CommentTab(
            comment = comment,
            onCommentChanged = { newComment ->
                comment = newComment // 작성된 코멘트 저장
            }
        )
    }
}

@Composable
fun MovieInfoTab(
    docId: String?,
    onMovieLoaded: (Movie) -> Unit,
    onRatingLoaded: (Float) -> Unit, // 기존 별점 전달
    onRatingChanged: (Float) -> Unit, // 별점 변경시 호출
    onCommentLoaded: (String) -> Unit
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
            isLoading.value = true
            val (fetchedRating, fetchedComment) = fetchUserRatingAndComment(it.uid, docId)
            userRating = fetchedRating // 사용자 별점 업데이트
            comment = fetchedComment // 사용자 코멘트 업데이트
            onRatingLoaded(fetchedRating) // 기존 별점 전달
            onCommentLoaded(fetchedComment) // 기존 코멘트 전달
            isLoading.value = false
        }
    }

    // 로딩 상태일 때 로딩 인디케이터 표시
    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    else {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 영화 정보 및 별점 표시
            Column(modifier = Modifier.fillMaxWidth()) {
                // 별점 상태를 별도로 관리
                var rating by remember { mutableStateOf(0f) }
                movie.value?.let {
                    MovieCard(
                        movie = it,
                        rating = rating,
                        showComments = false, // 코멘트 표시 여부 전달
                        onRatingChanged = { newRating ->
                            rating = newRating
                        },
                        comment = "",
                        isStarShow = false,
                        onCardClick = {}
                    )
                }
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
            value = comment, // 기존의 코멘트 뜨도록
            onValueChange = onCommentChanged,
            label = {
                if (comment.isEmpty()) {
                    Text("코멘트 작성") // 빈 코멘트일 때
                } else {
                    Text("코멘트 수정") // 기존 코멘트가 있을 때
                }
            },
            modifier = Modifier
                .fillMaxWidth() // 가로로 전체 크기 채우기
                .height(200.dp) // 높이 조절
                .padding(16.dp),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Done 버튼을 눌렀을 때의 처리
                }
            ),
            singleLine = false // 여러 줄 입력을 허용
        )
    }
}

fun saveRatingAndComment(
    movie: Movie,
    currentRating: Float,
    originalRating: Float,
    currentUser: FirebaseUser?,
    originalComment: String,
    currentComment: String
){
    currentUser?.let {
        val firestore = FirebaseFirestore.getInstance()
        val userId = it.uid
        val userCollection = firestore.collection("user").document(userId)


        firestore.collection("movieRated")
            .whereEqualTo("movie", movie.DOCID)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                // 기존 문서가 있으면 업데이트
                if (!documents.isEmpty) {
                    val existingDoc = documents.documents.first()

                    // 업데이트할 데이터 준비
                    val updatedData = mutableMapOf<String, Any>()
                    if (currentRating != originalRating) {
                        updatedData["score"] = currentRating
                    }
                    if (currentComment != originalComment) {
                        updatedData["comment"] = currentComment
                    }
                    if (updatedData.isNotEmpty()) {
                        updatedData["updatedTime"] = Timestamp.now()

                        // Firestore 업데이트 호출
                        firestore.collection("movieRated")
                            .document(existingDoc.id)
                            .update(updatedData)
                            .addOnSuccessListener {
                                println("Rating and comment updated successfully.")
                            }
                            .addOnFailureListener { e ->
                                println("Failed to update rating or comment: ${e.message}")
                            }
                    }
                    // 별점이 새로 부여됐다면 업데이트
                    /*if(currentRating != originalRating){
                        val existingDoc = documents.documents.first()
                        firestore.collection("movieRated")
                            .document(existingDoc.id)
                            .update(
                                "score", currentRating,
                                //"comment", comment, // comment 필드 추가
                                "updatedTime", Timestamp.now()
                            )
                    }
                    // 코멘트가 새로 부여됐다면 업데이트
                    if( existingDoc["comment"] != comment){
                        firestore.collection("movieRated")
                            .document(existingDoc.id)
                            .update(
                                //"score", currentRating,
                                "comment", comment, // comment 필드 추가
                                "updatedTime", Timestamp.now()
                            )
                    }
                    else{
                        firestore.collection("movieRated")
                            .document(existingDoc.id)
                            .update(
                                "comment", comment, // comment 필드 추가
                                "updatedTime", Timestamp.now()
                            )
                    }*/
                }
                else{
                    // 새 문서 생성
                    val newRating = hashMapOf(
                        "movie" to movie.DOCID,
                        "score" to currentRating,
                        "comment" to currentComment, // comment 필드 추가
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
}

suspend fun fetchUserRatingAndComment(userId: String, movieDocId: String?): Pair<Float, String> {
    return try {
        movieDocId?.let { docId ->
            // Firestore 쿼리를 통해 userId와 movie가 일치하는 문서 찾기
            val querySnapshot = FirebaseFirestore.getInstance()
                .collection("movieRated")
                .whereEqualTo("userId", userId) // userId와 일치하는 문서
                .whereEqualTo("movie", docId) // movie와 일치하는 문서
                .get()
                .await()

            // 일치하는 문서가 있는 경우
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                val score = document.getDouble("score")?.toFloat() ?: 0f
                val comment = document.getString("comment") ?: ""

                Pair(score, comment) // 별점과 코멘트를 반환
            } else {
                Pair(0f, "") // 일치하는 문서가 없는 경우 기본값 반환
            }
        } ?: Pair(0f, "") // movieDocId가 null인 경우 기본값 반환
    } catch (e: Exception) {
        e.printStackTrace() // 에러 로그 출력
        Pair(0f, "") // 에러 발생 시 기본값 반환
    }
}