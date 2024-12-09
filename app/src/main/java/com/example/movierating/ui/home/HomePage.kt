package com.example.movierating.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movierating.ui.theme.MovieRatingTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.example.movierating.ui.movieInfo.MovieViewModel
import com.example.movierating.ui.signIn.UserData
import com.example.movierating.ui.user.UserViewModel
import com.google.firebase.auth.FirebaseAuth

/** user Data 각 Page에서 불러오는 법
 * 1. Page 파라미터 부분에  userViewModel: UserViewModel = hiltViewModel(), 선언
 * 2. 함수 지역 변수로 val userData = userViewModel.userData.value 선언
 * 3. userData는 null값일 수도 있기 때문에 ?표시 필요
 */
@Composable
fun HomePage (
    modifier: Modifier = Modifier,
    goToDetailPage: (String) -> Unit,
    goToWorldCupPage: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    movieViewModel: MovieViewModel = hiltViewModel()
    ) {
    val moviesByGenre = movieViewModel.moviesByGenre.observeAsState(emptyMap())

    Column (
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 스크롤 가능하게 설정
    ) {
        AdBanner()
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WorldCupButton(onClickWorldCup = goToWorldCupPage)
            moviesByGenre.value.forEach { (genre, movies) ->
                MovieSlidingList(
                    title = genre,
                    movies = movies,
                    onMovieClick = { selectedMovie -> goToDetailPage(selectedMovie.DOCID) }
                )
            }
        }
    }
}

@Composable
fun AdBanner () {
    Column (
        modifier = Modifier
            .background(Color(0xFF2E2E2E))
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CineFeel",
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        )
        Image(
            painter = painterResource(id = R.drawable.img_home_admovie1),
            contentDescription = "Network image",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(269.dp, 348.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun MovieSlidingList (
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        )
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(movies) { movie ->
                Card (
                    modifier = Modifier
                        .clickable { onMovieClick(movie) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(movie.posters),
                        contentDescription = "movie",
                        modifier = Modifier
                            .size(86.dp, 123.dp),
                    )
                }

            }
        }
    }
}

@Composable
fun WorldCupButton (
    onClickWorldCup: () -> Unit
) {
    Row (
        modifier = Modifier
            .size(361.dp, 103.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFC6767), Color(0xFFFF947D))
                )
            )
            .clickable { onClickWorldCup() }
            .padding(16.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "영화 월드컵",
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.W700,
                    fontSize = 24.sp
                )
            )
            Text(
                text = "평가한 영화를 바탕으로 최애 영화 찾기",
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_worldcup),
            contentDescription = "worldCup",
            modifier = Modifier.size(80.dp, 80.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}