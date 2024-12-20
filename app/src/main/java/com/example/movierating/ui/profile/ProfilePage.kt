package com.example.movierating.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movierating.R
import com.example.movierating.data.Collections
import com.example.movierating.data.Movie
import com.example.movierating.data.MovieRated
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 프로필 정보 섹션
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.Gray
            ) { }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = userData.value?.username.toString(),
                    fontSize = 25.sp,
                )
                Text(
                    text = user?.email.toString(),
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f)) // 가변 공간 확보
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // 프로필 정보와 TabRow 사이 간격

        // Tab 화면 섹션
        ProfileTabNav(navController)
    }
}


@Composable
fun ProfileTabNav(navController: NavController) {
    val tabTitles = listOf("평가", "보고싶어요", "컬렉션")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // TabRow 영역 (IntrinsicSize 제거, 중앙 정렬 유지)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth() // 화면 너비에 맞춤
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // 탭 아래 간격

            // 탭에 따른 화면 전환
            when (selectedTabIndex) {
                0 -> RatingTab(navController) // 평가 화면
                1 -> WatchlistTab(navController) // 보고싶어요 화면
                2 -> CollectionTab(navController = navController)
            }
        }
    }
}

// 12-07 추가됨 fetchCollectionMoviesFromFirestore

suspend fun fetchCollectionMoviesFromFirestore(): List<Collections> {
    val db = FirebaseFirestore.getInstance() // Firestore 인스턴스
    val collectionsRef = db.collection("collections")

    return try {
        val querySnapshot = collectionsRef.get().await()
        if (querySnapshot.isEmpty) {
            emptyList()
        } else {
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(Collections::class.java)
            }
        }
    } catch (exception: Exception) {
        Log.e("FirestoreDebug", "Error fetching 'movieRated' collection", exception)
        emptyList()
    }
}

suspend fun fetchMoviesFromFirestore(): List<Movie> {
    val db = FirebaseFirestore.getInstance() // Firestore 인스턴스
    val moviesRef = db.collection("movies") // 'movies' 컬렉션 참조

    return try {
        // Firestore에서 데이터를 가져와서 Movie 객체 리스트로 변환
        val querySnapshot = moviesRef.get().await() // 비동기 호출. querySnapshot에는 movies 컬렉션에서 받아온 정보가 담김
        querySnapshot.documents.mapNotNull { document ->    // querySnapshot.documents => document의 리스트 처럼 작동
            document.toObject(Movie::class.java) // Movie 데이터 클래스로 변환
        }
    } catch (exception: Exception) {
        // 오류 발생 시 빈 리스트 반환 또는 예외 처리
        emptyList()
    }
}

suspend fun fetchMovieRatedFromFirestore(): List<MovieRated> {
    val db = FirebaseFirestore.getInstance() // Firestore 인스턴스
    val moviesRef = db.collection("movieRated") // 'movieRated' 컬렉션 참조

    return try {
        val querySnapshot = moviesRef.get().await()
        if (querySnapshot.isEmpty) {
            Log.d("FirestoreDebug", "No documents found in 'movieRated' collection.")
            emptyList()
        } else {
            querySnapshot.documents.mapNotNull { document ->
                Log.d("FirestoreDebug", "Document found: ${document.id} -> ${document.data}")
                document.toObject(MovieRated::class.java)
            }
        }
    } catch (exception: Exception) {
        Log.e("FirestoreDebug", "Error fetching 'movieRated' collection", exception)
        emptyList()
    }
}


// 포스터 비율의 surface 에 사진 채우기
@Composable
fun MovieImage(imageUrl: String) {
    val posterWidth = 80.dp
    val posterHeight = 120.dp // 세로로 더 긴 비율 설정 (원래 포스터 비율에 맞춤)

    Surface(
        modifier = Modifier
            .size(width = posterWidth, height = posterHeight) // 고정된 크기 설정
            .clip(RoundedCornerShape(8.dp)), // 이미지 둥근 모서리 처리
        color = Color.LightGray // 이미지가 로드되지 않을 경우 색상
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Movie Thumbnail",
            contentScale = ContentScale.Crop // 이미지 비율 유지하며 채우기
        )
    }
}

@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    initialRating: Float = 0f,
    onRatingChanged: (Float) -> Unit = {},
    isStarFixed: Boolean = false
) {
    var rating by remember { mutableStateOf(initialRating) }

    val fullStar: Painter = painterResource(id = R.drawable.ic_fullstar)
    val halfStar: Painter = painterResource(id = R.drawable.ic_halfstar)

    Row(modifier = modifier) {
        for (i in 1..5) {
            val starPainter = when {
                i <= rating -> fullStar
                i - 0.5f <= rating -> halfStar
                else -> painterResource(id = R.drawable.ic_emptystar)
            }

            Image(
                painter = starPainter,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .then(
                        if (!isStarFixed) {
                            Modifier.clickable {
                                rating = if (rating == i.toFloat()) i - 0.5f else i.toFloat()
                                onRatingChanged(rating) // 변경된 별점 전달
                            }
                        } else {
                            Modifier // 빈 Modifier (클릭 불가)
                        }
                    )
            )
        }
    }
}