package com.example.movierating

import android.os.Bundle
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movierating.ui.theme.MovieRatingTheme
import androidx.navigation.compose.rememberNavController
import com.example.movierating.ui.BottomNavigationBar
import com.example.movierating.ui.home.HomePage
import com.example.movierating.ui.movieInfo.MovieInfo
import com.example.movierating.ui.profile.LikePage
import com.example.movierating.ui.profile.ProfilePage
import com.example.movierating.ui.rate.RatePage
import com.example.movierating.ui.search.SearchPage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MovieRatingTheme {
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
                            RatePage(modifier = Modifier.padding(innerPadding))
                        }
                        composable("search") {
                            SearchPage(modifier = Modifier.padding(innerPadding))
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