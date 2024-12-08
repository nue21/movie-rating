package com.example.movierating.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.movierating.data.Movie
import com.example.movierating.data.MovieRated
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private var _currentMathUp = mutableStateOf<List<WorldCupMovie>>(emptyList())
    val currentMathUp: State<List<WorldCupMovie>> = _currentMathUp

    private var _worldCupResult = mutableStateOf<List<WorldCupMovie>>(emptyList())
    val worldCupResult: State<List<WorldCupMovie>> = _worldCupResult

    private val roundList = listOf(16,32,64)
    private var _nextMathUp = mutableStateOf<List<WorldCupMovie>>(emptyList())

    private var _temp  = mutableStateOf<List<WorldCupMovie>>(emptyList())

    fun chooseMovie(num: Int) {
        when(_roundCnt.value - _round.value) {
            3 -> {
                _nextMathUp.value += _currentMathUp.value[(num+1)%2]
                _temp.value += _currentMathUp.value[num]
                if(_gameCnt.value == 1) {
                    _gameCnt.value++
                    _currentMathUp.value = listOf(_worldCupMovies.value[2], _worldCupMovies.value[3])
                }
                else {
                    _gameCnt.value = 1
                    _roundCnt.value++
                    _currentMathUp.value = listOf(_nextMathUp.value[0], _nextMathUp.value[1])
                }
            }
            4 -> {
                _worldCupResult.value =
                    listOf(_currentMathUp.value[num], _currentMathUp.value[(num + 1) % 2])
                _gameCnt.value = 1
                _roundCnt.value++
                _currentMathUp.value = listOf(_temp.value[0], _temp.value[1])
            }
            5 -> {
                _worldCupResult.value =
                    listOf(_currentMathUp.value[num], _currentMathUp.value[(num + 1) % 2]) + _worldCupResult.value
                _state.value = WorldCupState.GameOver
                _roundCnt.value = 1
                _gameCnt.value = 1
            }
            else -> {
                if(roundList[_round.value] == _gameCnt.value * Math.pow(2.0, _roundCnt.value.toDouble()).toInt()){
                    _nextMathUp.value += _currentMathUp.value[num]
                    _roundCnt.value++
                    _gameCnt.value=1
                    _worldCupMovies.value = _nextMathUp.value
                    _nextMathUp.value = emptyList()
                    _currentMathUp.value = listOf(_worldCupMovies.value[0], _worldCupMovies.value[1])
                } else {
                    println("list: "+ _worldCupMovies.value)
                    println("gameCnt : ${_gameCnt.value} roundCnt : ${_roundCnt.value}")
                    _nextMathUp.value += _currentMathUp.value[num]
                    _gameCnt.value++
                    _currentMathUp.value = listOf(_worldCupMovies.value[(_gameCnt.value-1)*2], _worldCupMovies.value[(_gameCnt.value-1)*2+1])
                }
            }
        }
    }

    fun setGame(round: Int, movieRatedList: List<WorldCupMovie>) {
        _round.value = round
        _state.value = WorldCupState.InGame
        _gameCnt.value = 1
        _roundCnt.value = 1
        _worldCupMovies.value = movieRatedList
        _currentMathUp.value += _worldCupMovies.value[0]
        _currentMathUp.value += _worldCupMovies.value[1]
    }
}

sealed class WorldCupState {
    object  BeforeStart: WorldCupState()
    object  GameOver: WorldCupState()
    object  InGame: WorldCupState()
}
