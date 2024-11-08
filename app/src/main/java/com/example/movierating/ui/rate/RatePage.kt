package com.example.movierating.ui.rate
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movierating.R

data class Movie(
    val posterUrl: String,
    val title: String,
    val info: String,
    val comment: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatePage(
    modifier: Modifier = Modifier
) {
    val movies = listOf(
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/55/tn_DPK022735.jpg", "오드리", "2024 1시간 15분", "오드리 재밌다 (아직 안봄)"),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/51/tn_DPF029805.jpg", "프로이드의 라스트 세션", "2024 1시간 15분", "에고 이드 초에고 프로이트 화이팅"),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/50/tn_DPF029783.jpg", "10 라이브즈", "2024 1시간 15분", "나도 목숨 10개 가지고 싶다. 하나 주라."),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/46/tn_DPF029705.jpg", "굿 바이 크루얼 월드", "2024 1시간 15분", "잔인한 세상 미워\n 행복했으면 좋겠다."),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/43/tn_DPF029352.jpg", "키타로 탄생", "2024 1시간 15분", "복숭아동자 리메이크"),
        Movie("http://file.koreafilm.or.kr/thm/02/99/18/37/tn_DPF029035.jpg", "사랑은 비", "2024 1시간 15분", "가뭄")
    )

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
                text = "125",
                style = typography.displayMedium
            )
            Text(
                text = "평가한 영화 수",
                style = typography.titleMedium
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(movies) { movie ->
                    MovieCard(
                        moviePosterUrl = movie.posterUrl,
                        movieTitle = movie.title,
                        movieInfo = movie.info,
                        onRatingChanged = { rating ->
                            Log.d("RatePage", "영화 ${movie.title}의 별점이 변경되었습니다.")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    moviePosterUrl: String,
    movieTitle: String,
    movieInfo: String,
    onRatingChanged: (Float) -> Unit
) {
    var rating by remember { mutableStateOf(0f) } // 별점 상태 값 초기화

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        Row {
            MovieImage(imageUrl = moviePosterUrl)

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = movieTitle,
                    style = typography.titleLarge
                )
                Text(
                    text = movieInfo,
                    style = typography.titleMedium
                )
                StarRating(
                    initialRating = rating,
                    onRatingChanged = { newRating ->
                        rating = newRating
                        onRatingChanged(newRating) // 부모로 변경된 별점 전달
                    }
                )
            }
        }
    }
}

@Composable
fun MovieImage(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Movie Thumbnail",
        contentScale = ContentScale.Crop // 이미지 크기에 맞게 조정
    )
}

@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    initialRating: Float = 0f,
    onRatingChanged: (Float) -> Unit = {}
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
                    .clickable {
                        rating = if (rating == i.toFloat()) i - 0.5f else i.toFloat()
                        onRatingChanged(rating) // 변경된 별점 전달
                    }
            )
        }
    }
}
