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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movierating.ui.theme.MovieRatingTheme
import androidx.navigation.compose.rememberNavController
import com.example.movierating.ui.BottomNavigationBar

import com.example.movierating.ui.home.HomePage
import com.example.movierating.ui.home.WorldCupPage
import com.example.movierating.ui.movieInfo.AddCommentPage

import com.example.movierating.ui.movieInfo.AddCollectionPage

import com.example.movierating.ui.movieInfo.MovieDetailPage
import com.example.movierating.ui.profile.ProfilePage

import com.example.movierating.ui.profile.CollectionDetailPage

import com.example.movierating.ui.profile.WatchlistTab

import com.example.movierating.ui.rate.CommentPage
import com.example.movierating.ui.search.SearchPage
import com.example.movierating.ui.search.SearchResultPage
import com.example.movierating.ui.search.SearchViewModel
import com.example.movierating.ui.search.SearchViewModelFactory
import com.example.movierating.ui.signIn.GoogleAuthUiClient
import com.example.movierating.ui.signIn.SignInPage
import com.example.movierating.ui.signIn.SignInViewModel
import com.example.movierating.ui.user.UserViewModel
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch

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
                        val signInViewModel = viewModel<SignInViewModel>()
                        val signInState by signInViewModel.state.collectAsStateWithLifecycle()
                        // 로그인되어 있으면 바로 MainNav으로 이동
                        LaunchedEffect(key1 = Unit) {
                            println("집중"+userViewModel.userData.value)
                            if (userViewModel.userData.value != null)
                                navController.navigate("main")
                        }

                        // SignInPage의 onSignInClick 실행 후 launcher 실행
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        lifecycleScope.launch {
                                            // 로그인 성공 시 result.data에는 UserData
                                            // 로그인 실패 시 result.data에는 null
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            // 로그인 성공 여부에 따라 signInState 값을 바꿈
                                            signInViewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            }
                        )

                        // signInState가 성공일 때 MainNav으로 이동
                        LaunchedEffect(key1 = signInState.isSignInSuccessful) {
                            if(signInState.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "로그인 성공",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate("main")
                                signInViewModel.resetState()
                            }
                        }

                        SignInPage(
                            state = signInState,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
                    // 2. MainNav
                    composable("main") {
                        MainNavHost(
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()    // 로그아웃
                                    userViewModel.resetUserData()   // userData -> null
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed out",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("signIn")   // 화면 이동
                                }
                            }
                        )
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

fun NavGraphBuilder.homeGraph(navController: NavHostController, modifier: Modifier, onSignOut: () -> Unit
) {
    composable("home") {
        HomePage(modifier, onSignOut, goToWorldCupPage = { navController.navigate("worldCup") })
    }
    composable("worldCup") {
        WorldCupPage(modifier)
    }
}

fun NavGraphBuilder.rateGraph(navController: NavHostController, modifier: Modifier) {
    composable("rate") {
        CommentPage(modifier)
    }
    composable("movieDetail") {
        MovieDetailPage(modifier,  navController)
    }
    composable("addCollection") {
        AddCollectionPage()
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
    composable("collectionDetail"){
        CollectionDetailPage(modifier, navController)
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