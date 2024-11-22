package com.example.movierating.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
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
                    text = "닉네임",
                    fontSize = 25.sp,
                )
                Text(
                    text = "g2hyeong@gmail.com",
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
        TabScreen()
    }
}


@Composable
fun TabScreen() {
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
                0 -> RatingTabContent() // 평가 화면
                1 -> WatchlistTabContent() // 보고싶어요 화면
                2 -> CollectionTabContent() // 컬렉션 화면
            }
        }
    }
}

suspend fun fetchMoviesFromFirestore(): List<Movie> {
    val db = FirebaseFirestore.getInstance() // Firestore 인스턴스
    val moviesRef = db.collection("movies") // 'movies' 컬렉션 참조

    return try {
        // Firestore에서 데이터를 가져와서 Movie 객체 리스트로 변환
        val querySnapshot = moviesRef.get().await() // 비동기 호출
        querySnapshot.documents.mapNotNull { document ->
            document.toObject(Movie::class.java) // Movie 데이터 클래스로 변환
        }
    } catch (exception: Exception) {
        // 오류 발생 시 빈 리스트 반환 또는 예외 처리
        emptyList()
    }
}

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

@Composable
fun MovieCard(
    movie: Movie,
    rating: Float,
    showComments: Boolean,
    onRatingChanged: (Float) -> Unit
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
            // 영화 기본 정보 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp), // 패딩 조정
                verticalAlignment = Alignment.CenterVertically
            ) {
                MovieImage(imageUrl = movie.posters ?: "") // 포스터 이미지

                Spacer(modifier = Modifier.width(16.dp)) // 이미지와 텍스트 간격

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f) // 남은 공간 채우기
                ) {
                    // 영화 제목
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp // 폰트 크기 조정
                        )
                    )

                    // 부가 정보 (상영시간, 감독, 국가)
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
                    color = Color(0xFFE0E0E0), // 코멘트 위에 얇은 구분선 추가
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
                        text = "코멘트가 없습니다. 진짜 쓸 말이 없어서 그래요. 제가 LA에 있을때 먹었던 돼지국밥처럼 정말 말이 안 나오는 그런 상황이에요. 뭐라고 더 써야 세 줄 이상 나올까요? 생각해보니 줄바꿈을 하면 되는군요. 하하 이왕 썼는데 지우기도 아쉽고 그냥 자고싶고 막 그런데 그냥 그냥 편하게 편하게 살고 싶어요 하하하....",
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3, // 확장 여부에 따라 표시 줄 수 변경
                        overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis, // 말줄임표
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp // 글자 크기 조정
                        )
                    )
                }
            }
        }
    }
}

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
    onRatingChanged: (Float) -> Unit = {}
) {
    var rating by remember { mutableStateOf(initialRating) }

    val fullStar: Painter = painterResource(id = R.drawable.ic_fullstar)
    val emptyStar: Painter = painterResource(id = R.drawable.ic_emptystar)

    Row(modifier = modifier) {
        for (i in 1..5) {
            val starPainter = if (i <= rating) fullStar else emptyStar
            Image(
                painter = starPainter,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp) // 별 크기 증가 (기존 24.dp → 36.dp)
                    .clickable {
                        rating = if (rating == i.toFloat()) i - 0.5f else i.toFloat()
                        onRatingChanged(rating) // 별점 변경 시 전달
                    }
            )
        }
    }
}



@Composable
fun WatchlistTabContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "보고싶어요 화면")
    }
}

@Composable
fun CollectionTabContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "컬렉션 화면")
    }
}

