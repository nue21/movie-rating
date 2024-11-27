package com.example.movierating.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.R

@Composable
fun HomePage (
    modifier: Modifier = Modifier
) {
    val movieImgUrls = arrayOf(
        "http://file.koreafilm.or.kr/thm/02/99/18/55/tn_DPK022735.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/51/tn_DPF029805.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/50/tn_DPF029783.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/46/tn_DPF029705.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/43/tn_DPF029352.jpg",
        "http://file.koreafilm.or.kr/thm/02/99/18/37/tn_DPF029035.jpg"
    )

    Column (
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // 스크롤 가능하게 설정
    ) {
        Button(onClick = { /*TODO*/ }) {
            
        }
        AdBanner()
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WorldCupButton()
            MovieSlidingList(title = "최신 개봉작", imgUrls = movieImgUrls)
            MovieSlidingList(title = "최근 조회한 영화", imgUrls = movieImgUrls)
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
            text = "app logo",
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
    imgUrls: Array<String>
) {
    Column (
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
            items(imgUrls) { url ->
                Card (
                    modifier = Modifier
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(url),
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
fun WorldCupButton () {
    Row (
        modifier = Modifier
            .size(361.dp, 103.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFC6767), Color(0xFFFF947D))
                )
            )
            .padding(16.dp)
            .padding(horizontal = 10.dp)
            .clickable { /* todo */ },
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieRatingTheme {
        HomePage()
    }
}
