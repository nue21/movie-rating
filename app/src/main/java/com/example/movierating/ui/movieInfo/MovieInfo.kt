package com.example.movierating.ui.movieInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun MovieInfo(modifier: Modifier = Modifier, navController: NavHostController){
    // Firestore에서 가져온 영화 데이터를 담는 변수
    var movie by rememberSaveable { mutableStateOf<Movie?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }

    // Firestore에서 영화 데이터를 비동기적으로 읽어오기
    LaunchedEffect(Unit) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            val snapshot = firestore.collection("movies").limit(1).get().await() //우선 첫번째 영화데이터만 가져오도록 설정(추후 수정예정)
            if (snapshot.documents.isNotEmpty()) {
                val movieData = snapshot.documents.first().toObject(Movie::class.java)
                movie = movieData
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
        MovieInfoContent(movie!!, navController)
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
fun MovieInfoContent(movie: Movie, navController: NavHostController){
    val scrollState = rememberScrollState()
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // 스크롤 가능하게 설정
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(
                    onClick = {},
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
                    tint = if (i <= 3) Color.Red else Color.LightGray,
                    modifier = Modifier.size(40.dp)
                )
                if (i != 5) {
                    Spacer(modifier = Modifier.width(8.dp)) // 간격 설정
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
                onClick = { isFavorite = !isFavorite }
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

        // 마지막 여백 추가
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
        plots[0].replace("\n", "") // \n을 공백으로 치환
    } else {
        "한글 설명이 없습니다." // 플롯 데이터가 없는 경우 기본 메시지
    }
}

// 런타임을 '몇시간 몇분'으로 변환하는 함수
fun formatRuntime(runtime: String?): String {
    if (runtime.isNullOrBlank()) return "정보 없음"
    return try {
        val totalMinutes = runtime.toInt() // 런타임을 숫자로 변환
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        "${hours}시간 ${minutes}분"
    } catch (e: NumberFormatException) {
        "정보 없음"
    }
}