package com.example.movierating.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
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
import com.example.movierating.ui.rate.StarRating
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.time.format.TextStyle

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
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabScreen(navController = navController)
    }
}


@Composable
fun TabScreen(navController: NavController) {
    val tabTitles = listOf("평가", "보고싶어요", "컬렉션")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                2 -> CollectionTabContent(navController = navController)
            }
        }
    }
}

suspend fun fetchMoviesFromFirestore(): List<Movie> {
    val db = FirebaseFirestore.getInstance() // Firestore 인스턴스
    val moviesRef = db.collection("movies") // 'movies' 컬렉션 참조

    return try {
        val querySnapshot = moviesRef.get().await() // 비동기 호출. querySnapshot에는 movies 컬렉션에서 받아온 정보가 담김
        querySnapshot.documents.mapNotNull { document ->    // querySnapshot.documents => document의 리스트 처럼 작동
            document.toObject(Movie::class.java) // Movie 데이터 클래스로 변환
        }
    } catch (exception: Exception) {
        // 오류 발생 시 빈 리스트 반환 또는 예외 처리
        emptyList()
    }
}

// 영화 포스터, 별점, 코멘트가 담긴 카드
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
                        text = "코멘트가 없습니다. 진짜 쓸 말이 없어서 그래요. 제가 LA에 있을때 먹었던 돼지국밥처럼 정말 말이 안 나오는 그런 상황이에요. 뭐라고 더 써야 세 줄 이상 나올까요? 생각해보니 줄바꿈을 하면 되는군요. 하하 이왕 썼는데 지우기도 아쉽고 그냥 자고싶고 막 그런데 그냥 그냥 편하게 편하게 살고 싶어요 하하하....",
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
fun CollectionCard(
    movies: List<Movie>,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEdit: Boolean,
) {
    var title = remember { mutableStateOf("컬렉션") }
    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 0.dp, // 선택 여부에 따라 테두리 두께 변경
                color = if (isSelected) Color.Gray else Color.White, // 선택 여부에 따라 색상 변경
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color(0xFFF9F9F9))
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 영화 포스터 섹션
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // movies가 비어있는 경우 대체 UI 출력
                if (movies.isNotEmpty() && movies[0].posters != null) {
                    movies[0].posters?.let { MovieImage(it) }
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 120.dp) // 고정 크기 설정
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray), // 회색 배경
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image",
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // 컬렉션 제목
            if (!isEdit) {
                Text(
                    text = title.value,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            else {
                TextField(
                    value = title.value,
                    onValueChange = {title.value = it},
                    modifier = Modifier.width(100.dp).height(56.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                    placeholder = {Text(text = title.value)}
                )
            }
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