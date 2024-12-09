package com.example.movierating.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.example.movierating.data.MovieRated
import com.example.movierating.ui.signIn.UserData
import com.example.movierating.ui.user.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupPage (
    navController: NavController,
    userViewModel: UserViewModel,
    worldCupViewModel: WordlCupViewModel
) {
    var isLoading by remember {  mutableStateOf(true)  }
    val userData by userViewModel.userData.observeAsState() // LiveData를 State로 변환
    val userMovieRated = userData?.movieRatedList
    val worldCupMovies = remember { mutableStateOf<List<WorldCupMovie>>(emptyList()) }
    val roundList: List<Int> = listOf(16, 32, 64)


    LaunchedEffect(userMovieRated) {
        println(userMovieRated)
        isLoading = true
        if (userMovieRated.isNullOrEmpty()){
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val fetchedMovies = userMovieRated.mapNotNull { movieRatedId ->
                fetchWorldCupMovie(movieRatedId)
            }

            worldCupMovies.value = fetchedMovies
        }catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "영화 월드컵") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_worldcup2),
                    contentDescription = "worldCup",
                    modifier = Modifier.size(150.dp, 150.dp),
                    contentScale = ContentScale.FillWidth
                )
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading...", fontSize = 16.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 5.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (index in 0..2) {
                            ActionButton(
                                index = index,
                                enable = roundList[index] <= userViewModel.userData.value?.movieRatedList?.size!!,
                                onClick = {
                                    worldCupViewModel.setGame(index, worldCupMovies.value)
                                    navController.navigate("worldCupPlay/$index")
                                })
                        }

//                ActionButton(index = null, enable = userViewModel.userData.value?.movieRatedList?.size != 0, onClick = {})
                    }
                }
            }
        }

}

suspend fun fetchWorldCupMovie(movieRatedId: String): WorldCupMovie? {
    val db = FirebaseFirestore.getInstance()

    return try {
        // movieRated 문서 가져오기
        val movieRatedSnapshot = db.collection("movieRated")
            .document(movieRatedId)
            .get()
            .await()

        val movieRated = movieRatedSnapshot.toObject(MovieRated::class.java) ?: return null

        // movies 문서 가져오기
        val movieSnapshot = db.collection("movies")
            .document(movieRated.movie)
            .get()
            .await()
        println("가져옴 "+movieSnapshot.id)
        val movie = movieSnapshot.toObject(Movie::class.java) ?: return null

        // WorldCupMovie 생성
        WorldCupMovie(
            movieRated.movie,
            movie.title,
            movie.posters,
            movieRated.comment,
            movieRated.updatedTime,
            movieRated.score
        )
    } catch (e: Exception) {
        Log.e("FetchWorldCupMovies", "Error fetching movie: ${e.message}", e)
        null
    }
}

@Composable
fun ActionButton (
    index: Int,
    enable: Boolean,
    onClick: () -> Unit
) {
    val roundList: List<Int> = listOf(16, 32, 64)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(
                brush = if (enable) Brush.linearGradient(
                    colors = listOf(Color(0xFFFC6767), Color(0xFFFF947D))
                ) else
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFC2C2C2), Color(0xFFC2C2C2))
                    )
            )
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 8.dp),
    ) {
        Text(
            text = "${roundList[index]}강",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MovieRatingTheme {
//        WorldCupPage()
//    }
//}