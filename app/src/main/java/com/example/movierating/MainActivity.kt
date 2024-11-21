package com.example.movierating

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movierating.ui.theme.MovieRatingTheme
import androidx.navigation.compose.rememberNavController
import com.example.movierating.service.MovieService
import com.example.movierating.ui.BottomNavigationBar
import com.example.movierating.ui.home.HomePage
import com.example.movierating.ui.movieInfo.MovieInfo
import com.example.movierating.ui.profile.LikePage
import com.example.movierating.ui.profile.ProfilePage
import com.example.movierating.ui.rate.CommentPage
import com.example.movierating.ui.rate.RatePage
import com.example.movierating.ui.search.SearchPage
import com.example.movierating.ui.search.SearchResultPage
import com.example.movierating.ui.search.SearchViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieRatingTheme {
                val navController = rememberNavController()

                val searchViewModel = viewModel<SearchViewModel>()
                searchViewModel.setSharedPreferences(this)

                /* 파이어스토어에 영화 데이터 저장
                val movieService = MovieService()
                val inputStream = resources.openRawResource(R.raw.movie_2024)
                val json = movieService.readFile(inputStream)
                val data = movieService.parseMoviesFromJson(json)
                movieService.saveMoviesToFirestore(data)*/

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomePage(modifier = Modifier.padding(innerPadding))
                        }
                        composable("rate") {
                            CommentPage(modifier = Modifier.padding(innerPadding))
                        }
                        composable("search") {
                            SearchPage(
                                modifier = Modifier.padding(innerPadding),
                                searchViewModel,
                                goToResultPage = { navController.navigate("searchResult") }
                            )
                        }
                        composable("profile") {
                            ProfilePage(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable(route = "info") {
                            MovieInfo(modifier = Modifier.padding(innerPadding))
                        }
                        composable(route = "like") {
                            LikePage(modifier = Modifier.padding(innerPadding))
                        }
                        composable("searchResult") {
                            SearchResultPage(
                                modifier = Modifier.padding(innerPadding),
                                searchViewModel,
                                backToSearchPage = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieRatingTheme {
        Greeting("Android")
    }
}