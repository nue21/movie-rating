package com.example.movierating.ui.movieInfo

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.example.movierating.ui.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import java.time.LocalDateTime

@Composable
fun MovieDetailPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    docId: String
){
    var movie by remember{ mutableStateOf<Movie?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var userRating by rememberSaveable { mutableStateOf(0f) }
    val user = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userRepository = UserRepository(auth, firestore)

    LaunchedEffect(docId) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            val document = firestore.collection("movies").document(docId).get().await()
            if (document.exists()) {
                val movieData = document.toObject(Movie::class.java)
                movieData?.let {
                    movie = it.copy(DOCID = document.id) // 문서 ID를 DOCID에 할당
                }
            }
            // 사용자 별점 불러오기
            user?.let {
                val userId = it.uid
                val ratingDoc = firestore.collection("movieRated")
                    .whereEqualTo("movie", docId)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                if (!ratingDoc.isEmpty) {
                    val ratingData = ratingDoc.documents.first()
                    userRating = ratingData.getDouble("score")?.toFloat() ?: 0f
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // 로딩 상태 표시
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...", fontSize = 16.sp)
        }
    } else if (movie != null) {
        MovieInfoContent(
            movie!!,
            userRating,
            navController,
            onRatingChanged = { newRating ->
                saveRating(movie!!, newRating, auth.currentUser) // 별점 저장 함수 호출
            },
            userRepository = userRepository,
            userId = auth.currentUser?.uid ?: ""
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Failed to load movie data.", fontSize = 16.sp)
        }
    }
}

@Composable
fun MovieInfoContent(
    movie: Movie,
    initialRating: Float,
    navController: NavHostController,
    onRatingChanged: (Float) -> Unit,
    userRepository: UserRepository,
    userId: String
){
    val scrollState = rememberScrollState()
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var rating by rememberSaveable { mutableStateOf(initialRating) }

    LaunchedEffect(userId) {
        val userDoc = FirebaseFirestore.getInstance()
            .collection("user")
            .document(userId)
            .get()
            .await()
        val userData = userDoc.data
        val wishList = userData?.get("wishList") as? List<String> ?: emptyList()
        isFavorite = wishList.contains(movie.DOCID)
    }

    // 버튼 클릭 시 wishList 업데이트
    fun toggleFavorite() {
        isFavorite = !isFavorite
        val addToWishList = isFavorite
        userRepository.updateWishList(userId, movie.DOCID, addToWishList) { success ->
            if (success) {
                isFavorite = addToWishList
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = Color.Transparent,
            contentColor = Color.Black,
            elevation = 0.dp,
            modifier = Modifier.height(56.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.DarkGray),
        ){
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    painter = rememberAsyncImagePainter(movie.posters ?: ""),
                    contentDescription = "movie",
                    modifier = Modifier
                        .width(180.dp)
                        .height(260.dp)
                        .size(120.dp, 123.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        //.padding(16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = movie.title, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2004 · ${movie.genre}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = formatRuntime(movie.runtime), fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // 별점
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 1..5) {
                Icon(
                    painter = painterResource(R.drawable.ic_fullstar),
                    contentDescription = "Star Rating",
                    tint = if (i <= rating) Color.Red else Color.LightGray,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            rating = i.toFloat()
                            onRatingChanged(rating)
                        }
                )
                if (i != 5) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 버튼들 ("보고싶어요", "코멘트", "컬렉션")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                label = "보고싶어요",
                backgroundColor = Color(0xFFE0E0E0),
                onClick = { toggleFavorite() }
            )
            ActionButton(
                icon = Icons.Default.Edit,
                label = "코멘트",
                backgroundColor = Color(0xFFE0E0E0),
                onClick = {}
            )
            ActionButton(
                icon = Icons.Default.Menu,
                label = "컬렉션",
                backgroundColor = Color(0xFFE0E0E0),
                onClick = { navController.navigate("addCollection") }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 영화 설명
        val koreanPlot = getKoreanPlot(movie.plots)
        Text(
            text = koreanPlot,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 14.sp,
            color = if (koreanPlot == "한글 설명이 없습니다.") Color.Gray else Color.Black
        )


        Spacer(modifier = Modifier.height(30.dp))

        // 감독 및 배우 정보
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = "감독", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = movie.directors.joinToString(", "){it.replace("\n", " ")},
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "배우", fontSize = 14.sp, color = Color.Gray)
            Text(text = movie.actors.joinToString(", "), fontSize = 14.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(105.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (icon == Icons.Filled.Favorite) Color.Red else Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, color = Color.Black)
        }
    }
}

// 한글 플롯만 추출하는 함수
fun getKoreanPlot(plots: List<String>): String {
    return if (plots.isNotEmpty()) {
        plots[0].replace("\n", "")
    } else {
        "한글 설명이 없습니다."
    }
}

// 런타임 변환 함수
fun formatRuntime(runtime: String?): String {
    if (runtime.isNullOrBlank()) return "정보 없음"
    return try {
        val totalMinutes = runtime.toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        "${hours}시간 ${minutes}분"
    } catch (e: NumberFormatException) {
        "정보 없음"
    }
}

//
private fun saveRating(movie: Movie, score: Float, user: FirebaseUser?) {
    val firestore = FirebaseFirestore.getInstance()
    user?.let {
        val userId = it.uid
        val movieRatedCollection = firestore.collection("movieRated")

        movieRatedCollection
            .whereEqualTo("movie", movie.DOCID)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // 기존 문서가 있으면 업데이트
                    val existingDoc = documents.documents.first()
                    movieRatedCollection.document(existingDoc.id).update(
                        "score", score,
                        "updatedTime", Timestamp.now()
                    )
                } else {
                    // 새 문서 생성
                    val newRating = hashMapOf(
                        "movie" to movie.DOCID,
                        "score" to score,
                        "userId" to userId,
                        "updatedTime" to Timestamp.now()
                    )
                    movieRatedCollection.add(newRating)
                }
            }
    }
}