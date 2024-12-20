package com.example.movierating.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movierating.data.Movie
import com.example.movierating.ui.signIn.UserData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// 이전 검색기록 (로컬 스토리지 저장)을 가져오기 위해선 SharedPreferences기능 필요
// > sharedPreferences을 초기화하기 위해선 MainActivity의 context 필요
// > context는 생성자 변수로 그냥 선언할 수 없어서 하단에 있는 SearchViewModelFactory 사용해서 초기화
// => context를 받을 필요없으면 이와같이 할 필요 없음!
class SearchViewModel(private val context: Context) :ViewModel() {
    private var sharedPreferences by mutableStateOf<SharedPreferences>(context.getSharedPreferences("search_history", Context.MODE_PRIVATE))
    // 사용자 입력을 받는 StateFlow
    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput
    // 이전 검색어
    private var _searchHistory = mutableStateOf<List<String>>(emptyList())
    val searchHistory: State<List<String>> = _searchHistory
    // 영화 목록을 담는 StateFlow
    private val _resultMovies = mutableStateOf<List<Movie>>(emptyList())
    val resultMovies: State<List<Movie>> = _resultMovies
    // Firestore 인스턴스
    private val moviesRef = FirebaseFirestore.getInstance().collection("movies")

    // JSON 문자열을 리스트로 변환하여 불러오는 함수
    fun loadSearchHistory() {
        // local에 저장된 search history JSON 형식으로 가져오기
        val jsonString = sharedPreferences.getString("search_history", null)

        if (jsonString != null) {
            val type = object : TypeToken<List<String>>() {}.type
            _searchHistory.value = Gson().fromJson(jsonString, type)  // JSON 문자열을 리스트로 변환
        } else {
            _searchHistory.value = emptyList() // 저장된 데이터가 없으면 빈 리스트 반환
        }
    }

    // 검색어 업데이트
    fun updateSearchInput(newString: String) {
        _searchInput.value = newString
    }

    // search history 리셋 빈배열로 만들기
    fun resetSearchHistory() {
        saveSearchHistory(sharedPreferences, emptyList())
        _searchHistory.value = emptyList()
    }

    // JSON 문자열을 리스트로 변환하여 불러오는 함수
    @RequiresApi(35)
    fun updateSearchHistory() {
        val updatedHistory = _searchHistory.value.toMutableList()
        // 검색 기록에 있는 검색어를 검색했으면 지우고 맨앞에 추가
        if(searchInput.value in _searchHistory.value) updatedHistory.remove(searchInput.value)
        // 검색 기록이 21개 이상이면 가장 마지막 검색어를 지움
        if (updatedHistory.size > 20) updatedHistory.removeLast()
        // 새로운 검색어를 리스트 앞에 추가
        updatedHistory.add(0, searchInput.value)

        saveSearchHistory(sharedPreferences, updatedHistory)
        val jsonString = sharedPreferences.getString("search_history", null)

        if (jsonString != null) {
            val type = object : TypeToken<List<String>>() {}.type
            _searchHistory.value = Gson().fromJson(jsonString, type) // JSON 문자열을 리스트로 변환
        } else {
            _searchHistory.value = emptyList() // 저장된 데이터가 없으면 빈 리스트 반환
        }
    }

    // 리스트를 JSON으로 저장하는 함수
    private fun saveSearchHistory(sharedPreferences: SharedPreferences, searchHistory: List<String>) {
        val editor = sharedPreferences.edit()
        val jsonString = Gson().toJson(searchHistory) // 리스트를 JSON 문자열로 변환
        editor.putString("search_history", jsonString)
        editor.commit()
    }

    // searchInput 변경 시마다 Firestore에서 쿼리 실행
    init {
        viewModelScope.launch {
            searchInput
                .debounce(500)
                .collect { input ->
                    println("searchInput "+input)
                // 입력값이 비어있으면 바로 반환
                if (input.isBlank()) {
                    _resultMovies.value = emptyList()
                    return@collect
                }

                // 검색어에 맞는 쿼리 실행
                searchMovies(input)
            }
        }
    }

    // 영화 검색 함수
    private suspend fun searchMovies(input: String) {
        val querySnapshot = moviesRef
            .whereGreaterThanOrEqualTo("title", input)
            .whereLessThanOrEqualTo("title", input + '\uf8ff')
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            Log.d("MovieSearch", "No movies found: $input")
            _resultMovies.value = emptyList()
        } else {
            val movieList = querySnapshot.mapNotNull { document ->
                document.toObject(Movie::class.java)
            }
            _resultMovies.value = movieList
        }
    }

}

class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}