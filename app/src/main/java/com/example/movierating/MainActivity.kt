package com.example.movierating

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movierating.ui.theme.MovieRatingTheme
import androidx.navigation.compose.rememberNavController
import com.example.movierating.service.MovieService
import com.example.movierating.ui.BottomNavigationBar
import com.example.movierating.ui.LoginPage
import com.example.movierating.ui.home.HomePage
import com.example.movierating.ui.home.WorldcupPage
import com.example.movierating.ui.movieInfo.MovieInfo
import com.example.movierating.ui.profile.LikePage
import com.example.movierating.ui.profile.ProfilePage
import com.example.movierating.ui.rate.CommentPage
import com.example.movierating.ui.rate.RatePage
import com.example.movierating.ui.search.SearchPage
import com.example.movierating.ui.search.SearchResultPage
import com.example.movierating.ui.search.SearchViewModel
import com.example.movierating.ui.search.SearchViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val isLoggedIn = mutableStateOf(auth.currentUser!=null)

    // 새로운 방식으로 Activity 결과 처리
    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("MainActivity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    // 로그인 성공 후 처리
                    isLoggedIn.value = true
                } else {
                    // 로그인 실패 처리
                    isLoggedIn.value = false
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        // 구글 로그인 옵션 설정
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        // 로그인 버튼 클릭 시 호출
        val signInIntent = googleSignInClient.signInIntent
        setContent {
            MovieRatingTheme {
                // 로그인 여부에 따른 페이지 분리
                if (isLoggedIn.value) {
                    MainNavHost()
                } else {
                    LoginPage(onClickLogin = { signInLauncher.launch(signInIntent) })
                }
            }
        }
    }
}

@Composable
fun MainNavHost () {
    val navController = rememberNavController()

    // search 뷰모델 초기화
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(LocalContext.current)
    )
    searchViewModel.loadSearchHistory()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home") {
            homeGraph(navController, Modifier.padding(innerPadding))
            searchGraph(navController = navController, modifier = Modifier.padding(innerPadding), searchViewModel)
            rateGraph(navController, Modifier.padding(innerPadding))
            profileGraph(navController, Modifier.padding(innerPadding))
        }
    }
}

fun NavGraphBuilder.homeGraph(navController: NavHostController, modifier: Modifier) {
    composable("home") {
        HomePage(modifier)
    }
    composable("worldcup") {
        WorldcupPage(modifier)
    }
}

fun NavGraphBuilder.rateGraph(navController: NavHostController, modifier: Modifier) {
    composable("rate") {
        CommentPage(modifier)
    }
}


fun NavGraphBuilder.searchGraph(navController: NavHostController, modifier: Modifier, searchViewModel: SearchViewModel) {
    composable("search") {
        SearchPage(
            modifier,
            searchViewModel,
            goToResultPage = { navController.navigate("searchResult") }
        )
    }
    composable("searchResult") {
        SearchResultPage(
            modifier,
            searchViewModel,
            backToSearchPage = { navController.navigateUp() }
        )
    }
}

fun NavGraphBuilder.profileGraph(navController: NavHostController, modifier: Modifier) {
    composable("profile") {
        ProfilePage(modifier, navController = navController)
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