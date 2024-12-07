package com.example.movierating

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movierating.ui.theme.MovieRatingTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movierating.data.Movie
import com.example.movierating.service.MovieService
import com.example.movierating.ui.BottomNavigationBar

import com.example.movierating.ui.home.HomePage
import com.example.movierating.ui.home.WorldCupPage
import com.example.movierating.ui.movieInfo.AddCommentPage

import com.example.movierating.ui.movieInfo.AddCollectionPage

import com.example.movierating.ui.movieInfo.MovieDetailPage
import com.example.movierating.ui.profile.ProfilePage

import com.example.movierating.ui.profile.CollectionDetailPage

import com.example.movierating.ui.profile.WatchlistTab

import com.example.movierating.ui.rate.RatePage
import com.example.movierating.ui.search.SearchPage
import com.example.movierating.ui.search.SearchResultPage
import com.example.movierating.ui.search.SearchViewModel
import com.example.movierating.ui.search.SearchViewModelFactory
import com.example.movierating.ui.signIn.GoogleAuthUiClient
import com.example.movierating.ui.signIn.SignInEmailPage
import com.example.movierating.ui.signIn.SignInEmailViewModel
import com.example.movierating.ui.signUp.SignUpPage
import com.example.movierating.ui.signUp.SignUpViewModel
import com.example.movierating.ui.user.UserViewModel
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import java.io.FileInputStream

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // user 정보 view model
    private val userViewModel: UserViewModel by viewModels()

    // 로그인을 위한 UiClient
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MovieRatingTheme {
                val navController = rememberNavController()

                /** 화면 구성
                 * 1. SignInPage : 구글 로그인 버튼 (비로그인 상태 : userData가 null일 때 보여짐)
                 * 2. MainNav : 본격적인 서비스를 이용할 수 있는 navigator (로그인 상태 : userData가 null이 아님)
                 */
                NavHost(navController = navController, startDestination = "signIn") {
                    // 1. SignInPage
                    composable("signIn") {
                        val signInViewModel = viewModel<SignInEmailViewModel>()
                        val signInState by signInViewModel.state.collectAsStateWithLifecycle()

                        SignInEmailPage(navController = navController)
                    }
                    // 2. SignUpPage
                    composable("signUp"){
                        val signInViewModel = viewModel<SignUpViewModel>()
                        val signInState by signInViewModel.state.collectAsStateWithLifecycle()

                        SignUpPage(navController = navController)
                    }

                    // 3. MainNav
                    composable("main") {
                        val context = LocalContext.current
                        val lifecycleOwner = LocalLifecycleOwner.current
                        val coroutineScope = rememberCoroutineScope()

                        MainNavHost(
                            onSignOut = {
                                lifecycleOwner.lifecycleScope.launch {
                                    googleAuthUiClient.signOut()    // 로그아웃
                                    userViewModel.resetUserData()   // userData -> null
                                    Toast.makeText(
                                        context,
                                        "Signed out",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("signIn")   // 화면 이동
                                }
                            }
                        )
                        /*
                        // 이메일, 비밀번호로 자동 로그인 호출
                        LaunchedEffect(Unit) {
                            try {
                                val email = "g2hyeong@naver.com"
                                val password = "123456"
                                val userData = googleAuthUiClient.signInAndSaveUser(email, password)

                                if (userData != null) {
                                    Toast.makeText(context, "Welcome ${userData.userId}", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show()
                                    navController.navigate("signIn") // 로그인 실패 시 로그인 화면으로 이동
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }

                         */
                    }
                }
            }
        }
    }
}

@Composable
fun MainNavHost (
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()

    ////////////// MainNav에서 사용하게되는 View Model 여기서 초기화 (아마 대부분...) /////////////////
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
            homeGraph(navController, Modifier.padding(innerPadding), onSignOut)
            searchGraph(navController = navController, modifier = Modifier.padding(innerPadding), searchViewModel)
            rateGraph(navController, Modifier.padding(innerPadding))
            profileGraph(navController, Modifier.padding(innerPadding))
        }
    }
}

fun NavGraphBuilder.homeGraph(navController: NavHostController, modifier: Modifier, onSignOut: () -> Unit) {
    composable("home") {
        HomePage(
            modifier,
            onSignOut,
            goToWorldCupPage = { navController.navigate("worldCup") },
            goToDetailPage = { docId ->
                navController.navigate("movieDetail/$docId"){
                    launchSingleTop = true
                }
            }
        )
    }
    composable("worldCup") {
        WorldCupPage(modifier)
    }

    // MovieDetailPage 추가
    composable("movieDetail/{docId}") { backStackEntry ->
        val docId = backStackEntry.arguments?.getString("docId") ?: ""
        MovieDetailPage(modifier, navController, docId)
    }
}

fun NavGraphBuilder.rateGraph(navController: NavHostController, modifier: Modifier) {
    composable("rate") {
        RatePage(modifier)
    }
    composable("addCollection/{docId}") { backStackEntry -> // docId를 경로 변수로 추가
        val docId = backStackEntry.arguments?.getString("docId") ?: ""
        AddCollectionPage(modifier, navController, docId) // AddCollectionPage에 docId 전달
    }
    composable(route = "addComment/{docId}") { backStackEntry ->
        val docId = backStackEntry.arguments?.getString("docId") ?: ""
        AddCommentPage(navController, modifier, docId)
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
        ProfilePage(modifier, navController)
    }
    composable(
        "collectionDetailPage/{collectionId}",
        arguments = listOf(navArgument("collectionId") { type = NavType.StringType })
    ) { backStackEntry ->
        val collectionId = backStackEntry.arguments?.getString("collectionId")
        // collectionId를 사용하여 컬렉션 정보 로딩 등 처리
        CollectionDetailPage(modifier = modifier, navController = navController, collectionId = collectionId)
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