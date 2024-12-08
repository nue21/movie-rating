package com.example.movierating.ui.home

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.movierating.R
import com.example.movierating.data.Movie
import com.example.movierating.data.MovieRated
import com.example.movierating.service.MovieService
import com.example.movierating.ui.user.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class WordlCupViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<WorldCupState>(WorldCupState.BeforeStart)
    val state = _state.asStateFlow()
    private var _round = mutableStateOf<Int>(16)
    val round: State<Int> = _round
    private var _roundCnt = mutableStateOf<Int>(1)
    val roundCnt: State<Int> = _roundCnt
    private var _gameCnt = mutableStateOf<Int>(1)
    val gameCnt: State<Int> = _gameCnt
    private var _worldCupMovies = mutableStateOf<List<WorldCupMovie>>(emptyList())
    val worldCupMovies: State<List<WorldCupMovie>> = _worldCupMovies
    private var _currentMathUp = mutableStateOf<List<WorldCupMovie>>(emptyList())
    val currentMathUp: State<List<WorldCupMovie>> = _currentMathUp

    private var _nextMathUp = mutableStateOf<List<WorldCupMovie>>(emptyList())
    val nextMatchUp : State<List<WorldCupMovie>> =_nextMathUp

    fun setRound(round: Int) {
        _round.value = round
        _state.value = WorldCupState.InGame
    }

    fun chooseMovie(num: Int) {
        _nextMathUp.value += _currentMathUp.value[num]
        if(_gameCnt.value == _round.value / (_roundCnt.value+1)) {
            _worldCupMovies.value = _nextMathUp.value
            _gameCnt.value = 0
            _roundCnt.value++
        }
        _currentMathUp.value = emptyList()
        _currentMathUp.value += _worldCupMovies.value[0]
        _currentMathUp.value += _worldCupMovies.value[1]
    }

    fun fetchData(movieRatedList: List<String>?) {
        movieRatedList?.forEach {
            FirebaseFirestore.getInstance().collection("movieRated").document(it).get().addOnSuccessListener { document ->
                val movieRated: MovieRated = document.toObject(MovieRated::class.java)
                    ?: return@addOnSuccessListener
                FirebaseFirestore.getInstance().collection("movie").document(movieRated.movie).get().addOnSuccessListener {
                    val movie: Movie = it.toObject(Movie::class.java) ?: return@addOnSuccessListener
                    val worldCupMovie: WorldCupMovie = WorldCupMovie(movieRated.movie, movie.title, movie.posters, movieRated.comment,
                        movieRated.updatedTime.toString(), movieRated.score )
                    _worldCupMovies.value += worldCupMovie
                }
            }
        }
        _currentMathUp.value += _worldCupMovies.value[0]
        _currentMathUp.value += _worldCupMovies.value[1]
    }
}

sealed class WorldCupState {
    object  BeforeStart: WorldCupState()
    object  GameOver: WorldCupState()
    object  InGame: WorldCupState()
}